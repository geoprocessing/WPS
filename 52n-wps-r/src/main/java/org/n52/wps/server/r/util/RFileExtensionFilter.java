/*
 * Copyright (C) 2010-2018 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *       • Apache License, version 2.0
 *       • Apache Software License, version 1.0
 *       • GNU Lesser General Public License, version 3
 *       • Mozilla Public License, versions 1.0, 1.1 and 2.0
 *       • Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.wps.server.r.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.n52.wps.server.r.R_Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RFileExtensionFilter implements FileFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RFileExtensionFilter.class);

    @Override
    public boolean accept(File f) {
        try {
            File file = f.getCanonicalFile();
            if (file.isFile() && file.canRead()) {
                String name = file.getName();
                if (name.endsWith(R_Config.SCRIPT_FILE_SUFFIX)) {
                    return true;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Could not access file {}", f, e);
        }
        return false;
    }
}
