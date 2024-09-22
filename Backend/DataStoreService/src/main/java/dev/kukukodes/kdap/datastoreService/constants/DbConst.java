package dev.kukukodes.kdap.datastoreService.constants;

public class DbConst {
    public static class Collections {
        public static final String DATA_STORE = "dataStores";
        public static final String DATA_ENTRIES = "dataEntries";
    }

    public static class DocumentFields {
        public static class CommonFields {
            public static final String ID = "_id";
            public static final String NAME = "name";
            public static final String CREATED = "created";
            public static final String MODIFIED = "modified";
            public static final String DESCRIPTION = "description";
        }

        public static class DataStore {
            public static final String USER_ID = "userId";
            public static final String FIELDS = "fields";
        }
    }
}
