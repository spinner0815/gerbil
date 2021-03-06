/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.bat.annotator.WATAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class WATAnnotatorConfig extends AbstractAnnotatorConfiguration {
    public static final String ANNOTATOR_NAME = "WAT";
    private static final String WAT_CONFIG_FILE_PROPERTY_ENDPOINT = "org.aksw.gerbil.annotators.wat.endpoint";
    private static final String WAT_CONFIG_FILE_PROPERTY_PARAMETERS = "org.aksw.gerbil.annotators.wat.parameters";

    public WATAnnotatorConfig() {
        super(ANNOTATOR_NAME, true, new ExperimentType[] { ExperimentType.Sa2W});
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        String endpoint = GerbilConfiguration.getInstance().getString(WAT_CONFIG_FILE_PROPERTY_ENDPOINT);
        String urlParameters = GerbilConfiguration.getInstance().getString(WAT_CONFIG_FILE_PROPERTY_PARAMETERS);

        if (endpoint == null)
            return null;

        if (urlParameters == null)
            return new WATAnnotator(endpoint, "");
        else
            return new WATAnnotator(endpoint, urlParameters);
    }
}
