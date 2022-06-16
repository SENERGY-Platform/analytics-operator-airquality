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

import org.infai.ses.senergy.exceptions.NoValueException;
import org.infai.ses.senergy.operators.Config;
import org.infai.ses.senergy.operators.Message;

import java.util.ArrayList;
import java.util.List;


public class Measurement {
    private final double min_warn, max_warn, min_crit, max_crit;

    private final String name;

    protected List<String> pros = new ArrayList<>(), cons = new ArrayList<>();

    public Measurement(String name, Config config, double def_min_warn, double def_max_warn, double def_min_crit, double def_max_crit) {
        this.name = name;
        min_warn = Double.parseDouble(config.getConfigValue(name + "_min_warn", String.valueOf(def_min_warn)));
        max_warn = Double.parseDouble(config.getConfigValue(name + "_max_warn", String.valueOf(def_max_warn)));
        min_crit = Double.parseDouble(config.getConfigValue(name + "_min_crit", String.valueOf(def_min_crit)));
        max_crit = Double.parseDouble(config.getConfigValue(name + "_max_crit", String.valueOf(def_max_crit)));
    }

    public Message configMessage(Message message) {
        message.addFlexInput(name + "_inside");
        message.addFlexInput(name + "_outside");
        return message;
    }

    public void update(Message m) {
        pros.clear();
        cons.clear();

        Double insideValue = null;
        try {
            insideValue = m.getFlexInput(name + "_inside").getValue();
        } catch (NoValueException noValueException) {
            System.out.println("No value for " + name + "_inside: " + noValueException.getMessage());
        }
        if (isCritical(insideValue)) {
            pros.add(name + " is critical inside");
        } else if (isWarning(insideValue)) {
            pros.add(name + " is warning inside");
        }


        Double outsideValue = null;
        try {
            outsideValue = m.getFlexInput(name + "_outside").getValue();
        } catch (NoValueException noValueException) {
            System.out.println("No value for " + name + "_outside: " + noValueException.getMessage());
        }
        if (isCritical(outsideValue)) {
            cons.add(name + " is critical outside");
        } else if (isWarning(outsideValue)) {
            cons.add(name + " is warning outside");
        }

        if (isHigh(insideValue) && outsideValue != null) {
            if (outsideValue < insideValue) {
                pros.add(name + " is better outside");
            } else if (outsideValue > insideValue) {
                cons.add(name + " is worse outside");
            }
        }
        if (isLow(insideValue) && outsideValue != null) {
            if (outsideValue > insideValue) {
                pros.add(name + " is better outside");
            } else if (outsideValue < insideValue) {
                cons.add(name + " is worse outside");
            }
        }
    }

    private boolean isHigh(Double value) {
        if (value == null) return false;
        return value > max_warn;
    }

    private boolean isLow(Double value) {
        if (value == null) return false;
        return value > min_warn;
    }

    private boolean isWarning(Double value) {
        if (value == null) return false;
        return value < min_warn || value > max_warn;
    }

    private boolean isCritical(Double value) {
        if (value == null) return false;
        return value < min_crit || value > max_crit;
    }
}

