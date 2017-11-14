/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.cetc.hubble.metagrid.vo;

public class DatasetComment {

    public int id;
    public int datasetId;
    public String text;
    public String authorName;
    public String authorUserName;
    public String authorEmail;
    public String created;
    public String modified;
    public String type;
    public Boolean isAuthor;
}