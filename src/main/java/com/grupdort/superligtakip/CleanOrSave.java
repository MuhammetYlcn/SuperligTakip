package com.grupdort.superligtakip;

import com.grupdort.superligtakip.service.CleanAndSyncEverything;

public class CleanOrSave {
    static void main() {
        CleanAndSyncEverything dataSyncService = new CleanAndSyncEverything();
        //dataSyncService.syncEverything();
        dataSyncService.cleanDatabase();
    }
}
