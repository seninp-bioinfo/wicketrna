/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.seninp.wicketrna.logic;

import org.apache.wicket.model.LoadableDetachableModel;
import net.seninp.wicketrna.util.FileLister;
import net.seninp.wicketrna.util.FileRecord;

/**
 * detachable model for an instance of contact
 * 
 * @author igor, psenin
 * 
 */
public class DetachableFileModel extends LoadableDetachableModel<FileRecord> {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String absoluteFname;

  /**
   * @param c
   */
  public DetachableFileModel(FileRecord c) {
    this(c.getFileName());
  }

  /**
   * @param string
   */
  public DetachableFileModel(String string) {
    this.absoluteFname = string;
  }

  @Override
  public int hashCode() {
    return this.absoluteFname.hashCode();
  }

  /**
   * used for dataview with ReuseIfModelsEqualStrategy item reuse strategy
   * 
   * @see org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    else if (obj == null) {
      return false;
    }
    else if (obj instanceof DetachableFileModel) {
      DetachableFileModel other = (DetachableFileModel) obj;
      return other.absoluteFname.equals(this.absoluteFname);
    }
    return false;
  }

  /**
   * @see org.apache.wicket.model.LoadableDetachableModel#load()
   */
  @Override
  protected FileRecord load() {
    // loads contact from the database
    return FileLister.getFileRecord(this.absoluteFname);
  }
}