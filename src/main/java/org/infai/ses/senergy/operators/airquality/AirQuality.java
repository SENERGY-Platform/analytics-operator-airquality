/*
 * Copyright 2022 InfAI (CC SES)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.infai.ses.senergy.operators.airquality;

import org.infai.ses.senergy.operators.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AirQuality extends BaseOperator {
    private final Measurement[] measurements;

    public AirQuality(Config config) {
        measurements = new Measurement[]{
                new Measurement("PM10", config, 0, 40, 0, 50), //µg/m³
                new Measurement("PM2.5", config, 0, 25, 0, 30), //µg/m³
                new Measurement("PM1", config, 0, 0, 0, 0), //µg/m³
                new Measurement("CO2", config, 0, 1000, 0, 5000), //ppm
                new Measurement("O2", config, 19, 30, 18, 50), //%
                new Measurement("SO2", config, 0, 125, 0, 350), //µg/m³
                new Measurement("CH4", config, 0, 1, 0, 4), //%
                new Measurement("NO2", config, 0, 40, 0, 200), //µg/m³
                new Measurement("CO", config, 0, 8, 0, 35), //µg/m³
                new Measurement("O3", config, 0, 0, 0, 180), //µg/m³
                new Measurement("Rn", config, 0, 250, 0, 1000), //Bq/m³
                new Measurement("relHum", config, 40, 50, 0, 1000), //%
                new Measurement("temp", config, 20, 22, 18, 26), //°C
                new Measurement("VOC", config, 0, 1, 0, 10), //mg/m³
                new Measurement("pressure", config, 0, 1100, 0, 1100), //hPa
                new Measurement("pollenAmbrosia", config, 0, 1, 0, 3), //DWD Level
                new Measurement("pollenBirke", config, 0, 1, 0, 3), //DWD Level
                new Measurement("pollenErle", config, 0, 1, 0, 3), //DWD Level
                new Measurement("pollenHasel", config, 0, 1, 0, 3), //DWD Level
                new Measurement("pollenBeifuss", config, 0, 1, 0, 3), //DWD Level
                new Measurement("pollenEsche", config, 0, 1, 0, 3), //DWD Level
                new Measurement("pollenRoggen", config, 0, 1, 0, 3), //DWD Level
                new Measurement("pollenGraeser", config, 0, 1, 0, 3), //DWD Level
        };
    }

    @Override
    public void run(Message message) {
        List<String> pros = new ArrayList<>();
        List<String> cons = new ArrayList<>();
        for (Measurement measurement : measurements) {
            measurement.update(message);
            pros.addAll(measurement.pros);
            cons.addAll(measurement.cons);
        }

        String advice;
        int adviceCode;
        if (pros.size() == 0) {
            advice = "No reason to open a window!";
            adviceCode = 0;
        } else if (cons.size() == 0) {
            advice = "Consider opening a window!";
            adviceCode = 1;
        } else {
            advice = pros.size() + " reason" + (pros.size() > 1 ? "s" : "") + " for and "
                    + cons.size() + " reason" + (cons.size() > 1 ? "s" : "") + " against opening a window!";
            adviceCode = 1;
        }

        message.output("pros", String.join(", ", pros));
        message.output("cons", String.join(", ", cons));
        message.output("advice", advice);
        message.output("advice_code", adviceCode);
    }

    @Override
    public Message configMessage(Message message) {
        for (Measurement measurement : measurements) {
            message = measurement.configMessage(message);
        }
        return message;
    }
}
