package de.drnutella.castigo.data.api.dataAdapter;

import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.data.MySQL;

import java.util.concurrent.ExecutorService;

class DataAdapter {
    static final ExecutorService executorService = Castigo.getOwnExecutorService();
    static final MySQL mysql = Castigo.getMySQL();
}
