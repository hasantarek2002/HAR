package com.example.harwithwekathreeactivity;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instances;

public class DataIntanceCreation {

    public Instances getDataInstance(){

        Attribute mean_x = new Attribute("mean_x");
        Attribute mean_y = new Attribute("mean_y");
        Attribute mean_z = new Attribute("mean_z");
        Attribute magnitude_mean = new Attribute("magnitude_mean");

        Attribute std_x = new Attribute("std_x");
        Attribute std_y = new Attribute("std_y");
        Attribute std_z = new Attribute("std_z");
        Attribute magnitude_std = new Attribute("magnitude_std");

        Attribute rms_x = new Attribute("rms_x");
        Attribute rms_y = new Attribute("rms_y");
        Attribute rms_z = new Attribute("rms_z");
        Attribute magnitude_rms = new Attribute("magnitude_rms");

        Attribute min_x = new Attribute("min_x");
        Attribute min_y = new Attribute("min_y");
        Attribute min_z = new Attribute("min_z");
        Attribute magnitude_min = new Attribute("magnitude_min");

        Attribute max_x = new Attribute("max_x");
        Attribute max_y = new Attribute("max_y");
        Attribute max_z = new Attribute("max_z");
        Attribute magnitude_max = new Attribute("magnitude_max");

        Attribute median_x = new Attribute("median_x");
        Attribute median_y = new Attribute("median_y");
        Attribute median_z = new Attribute("median_z");
        Attribute magnitude_median = new Attribute("magnitude_median");

        Attribute mad_x = new Attribute("mad_x");
        Attribute mad_y = new Attribute("mad_y");
        Attribute mad_z = new Attribute("mad_z");
        Attribute magnitude_mad = new Attribute("magnitude_mad");

        Attribute corr_xy = new Attribute("corr_xy");
        Attribute corr_yz = new Attribute("corr_yz");
        Attribute corr_xz = new Attribute("corr_xz");

        ArrayList<String> labels = new ArrayList<String>();

        labels.add("SITTING");
        labels.add("STANDING");
        labels.add("WALKING");

        Attribute cls = new Attribute("class", labels);

        ArrayList<Attribute> attributes = new ArrayList<Attribute>();

        attributes.add(mean_x);
        attributes.add(mean_y);
        attributes.add(mean_z);
        attributes.add(magnitude_mean);

        attributes.add(std_x);
        attributes.add(std_y);
        attributes.add(std_z);
        attributes.add(magnitude_std);

        attributes.add(rms_x);
        attributes.add(rms_y);
        attributes.add(rms_z);
        attributes.add(magnitude_rms);

        attributes.add(min_x);
        attributes.add(min_y);
        attributes.add(min_z);
        attributes.add(magnitude_min);

        attributes.add(max_x);
        attributes.add(max_y);
        attributes.add(max_z);
        attributes.add(magnitude_max);

        attributes.add(median_x);
        attributes.add(median_y);
        attributes.add(median_z);
        attributes.add(magnitude_median);

        attributes.add(mad_x);
        attributes.add(mad_y);
        attributes.add(mad_z);
        attributes.add(magnitude_mad);

        attributes.add(corr_xy);
        attributes.add(corr_yz);
        attributes.add(corr_xz);

        attributes.add(cls);
        Instances dataset = new Instances("Test-dataset", attributes, 0);
        return dataset;

    }
}
