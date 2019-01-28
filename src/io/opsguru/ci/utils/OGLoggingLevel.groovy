package io.opsguru.ci.utils

/* OpsGuru LoggingLevel */
enum OGLoggingLevel {
    fatal(0x0),
    error(0x1),
    warn(0x2),
    info(0x4),
    debug(0x8),
    trace(0x10),
    stage(0x20)

    private final int iLevel;

    OGLoggingLevel(int level) {
        this.iLevel = level
    }
    public int getValue() {
        return this.iLevel
    }
}