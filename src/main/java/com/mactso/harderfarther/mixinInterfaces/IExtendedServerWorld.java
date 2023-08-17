package com.mactso.harderfarther.mixinInterfaces;

import java.util.ArrayList;
import java.util.List;

public interface IExtendedServerWorld {

    boolean areListInitialized();
    void setListInitialized();
    ArrayList<Float> getDifficultySectionNumbers();
    ArrayList<List<String>> getDifficultySectionMobs();

}
