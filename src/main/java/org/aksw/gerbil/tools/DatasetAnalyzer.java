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
package org.aksw.gerbil.tools;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.C2WDataset;
import it.acubelab.batframework.problems.D2WDataset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.utils.DatasetMapping;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.apache.commons.io.IOUtils;

public class DatasetAnalyzer {

    public static void main(String[] args) {
        List<DatasetConfiguration> datasetConfigs = DatasetMapping.getDatasetConfigurations();
        PrintStream output = null;
        try {
            output = new PrintStream("datasetAnalyzation.log");
            DatasetAnalyzer analyzer = new DatasetAnalyzer(output);
            for (DatasetConfiguration config : datasetConfigs) {
                try {
                    analyzer.analyzeDataset(config);
                    SingletonWikipediaApi.getInstance().flush();
                } catch (GerbilException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    private PrintStream output;

    public DatasetAnalyzer(PrintStream output) {
        this.output = output;
    }

    public void analyzeDataset(DatasetConfiguration config) throws GerbilException {
        analyzeAsD2W(config);
        analyzeAsC2W(config);
    }

    private void analyzeAsC2W(DatasetConfiguration config) throws GerbilException {
        D2WDataset dataset = (D2WDataset) config.getDataset(ExperimentType.D2W);
        if (dataset == null) {
            return;
        }
        output.print("D2W dataset: " + config.getName());
        output.print(" size=" + dataset.getSize());
        List<HashSet<Annotation>> goldStandard = dataset.getD2WGoldStandardList();
        double averageAnnotation = 0;
        for (HashSet<Annotation> annotations : goldStandard) {
            averageAnnotation += annotations.size();
        }
        output.println(" Annotations=" + averageAnnotation);
        output.println(" avg.Annotations=" + (averageAnnotation / dataset.getSize()));
    }

    private void analyzeAsD2W(DatasetConfiguration config) throws GerbilException {
        C2WDataset dataset = (C2WDataset) config.getDataset(ExperimentType.C2W);
        if (dataset == null) {
            return;
        }
        output.print("C2W dataset: " + config.toString());
        output.print(" size=" + dataset.getSize());
        List<HashSet<Tag>> goldStandard = dataset.getC2WGoldStandardList();
        double averageAnnotation = 0;
        for (HashSet<Tag> annotations : goldStandard) {
            averageAnnotation += annotations.size();
        }
        output.println(" Tags=" + averageAnnotation);
        output.println(" avg.Tags=" + (averageAnnotation / dataset.getSize()));
    }
}
