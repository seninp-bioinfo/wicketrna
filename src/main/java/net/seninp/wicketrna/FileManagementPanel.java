package net.seninp.wicketrna;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import net.seninp.wicketrna.db.WicketRNADb;
import net.seninp.wicketrna.logic.PiretChangeEvent;
import net.seninp.wicketrna.logic.PiretChangeListener;
import net.seninp.wicketrna.logic.PiretProperties;
import net.seninp.wicketrna.security.PiretWebSession;
import net.seninp.wicketrna.util.StackTrace;

public class FileManagementPanel extends Panel implements PiretChangeListener {

  private static final long serialVersionUID = -6725615122875891173L;

  /**
   * fix those DTD warnings.
   */
  static {
    WicketTagIdentifier.registerWellKnownTagName(PANEL);
  }

  private InputForm iForm;

  @Override
  protected void onInitialize() {
    super.onInitialize();
  }

  public FileManagementPanel(String id, IModel<String> model) {

    super(id, model);

    final FeedbackPanel feedback = new FeedbackPanel("feedback-fmanagement");
    add(feedback);

    String username = ((PiretWebSession) AuthenticatedWebSession.get()).getUser();
    String userFolder = "";
    if (null != username) {
      userFolder = Paths
          .get(PiretProperties.getFilesystemPath(), WicketRNADb.getUser(username).getUser_folder())
          .toString();
    }

    iForm = new InputForm("inputForm", new FileListModel(userFolder), feedback);

    ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(iForm);
    feedback.setFilter(filter);
    feedback.setEscapeModelStrings(false);

    add(iForm);

  }

  /** form for processing the input. */
  private class InputForm extends Form<Object> {

    private static final long serialVersionUID = 1L;

    ArrayList<FileNameWrapper> data;
    ListView<FileNameWrapper> listView;
    IModel<ArrayList<FileNameWrapper>> filesModel;

    public InputForm(String name, IModel<ArrayList<FileNameWrapper>> fNameModel,
        IFeedback feedback) {

      super(name);

      filesModel = fNameModel;

      // add a nested list view; as the list is nested in the form, the form will
      // update all FormComponent childs automatically.
      data = filesModel.getObject();
      listView = new ListView<FileNameWrapper>("list_items", data) {
        private static final long serialVersionUID = 1L;

        protected void populateItem(ListItem<FileNameWrapper> item) {
          FileNameWrapper wrapper = (FileNameWrapper) item.getModelObject();
          item.add(new Label("name", wrapper.getName()));
          item.add(new CheckBox("check", new PropertyModel<Boolean>(wrapper, "selected")));
        }
      };
      filesModel.detach();

      listView.setReuseItems(true);
      listView.setOutputMarkupId(true);

      add(listView);

    }

    public void onSubmit() {

      //
      // if file is selected -- delete it
      for (FileNameWrapper entry : data) {
        if (entry.getSelected()) {
          try {
            Path path = Paths.get(entry.getPath());
            if (Files.exists(path)) {
              Files.delete(Paths.get(entry.getPath()));
              this.info("deleted " + entry.getPath());
            }
          }
          catch (IOException e) {
            System.err.println(StackTrace.toString(e));
          }
        }
      }

      //
      // remove the list from display
      listView.removeAll();
      listView.remove();

      //
      // re-load the files list
      data = filesModel.getObject();
      listView = new ListView<FileNameWrapper>("list_items", data) {
        private static final long serialVersionUID = 1L;

        protected void populateItem(ListItem<FileNameWrapper> item) {
          FileNameWrapper wrapper = (FileNameWrapper) item.getModelObject();
          item.add(new Label("name", wrapper.getName()));
          item.add(new CheckBox("check", new PropertyModel<Boolean>(wrapper, "selected")));
        }
      };
      filesModel.detach();

      listView.setReuseItems(true);
      listView.setOutputMarkupId(true);

      add(listView);

    }

  }

  @Override
  public void changeEventReceived(PiretChangeEvent evt) {
    iForm.onSubmit();
  }

}
