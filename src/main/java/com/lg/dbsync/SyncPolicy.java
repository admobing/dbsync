package com.lg.dbsync;

public class SyncPolicy {

    private int maxDirty;

    private int maxTimeout;

    public boolean updateCheck(Model model) {
        Model.DirtySignature signature = model.getDirtySignature();
        int dirtyCount = signature.getDirtyCount();
        if (dirtyCount == 0) {
            return false;
        }
        if (dirtyCount >= maxDirty) {
            return true;
        }

        if (System.currentTimeMillis() - signature.getUpdateAt() > maxTimeout) {
            return true;
        }

        return false;
    }

    public int getMaxDirty() {
        return maxDirty;
    }

    public int getMaxTimeout() {
        return maxTimeout;
    }

    public static class Build {

        private int maxDirty = 20;

        private int maxTimeout = 10;

        public Build maxDirty(int count) {
            this.maxDirty = count;
            return this;
        }

        public Build maxTimeout(int timeout) {
            this.maxTimeout = timeout * 1000;
            return this;
        }

        public SyncPolicy build() {
            SyncPolicy policy = new SyncPolicy();
            policy.maxDirty = this.maxDirty;
            policy.maxTimeout = this.maxTimeout;
            return policy;
        }

    }

}
