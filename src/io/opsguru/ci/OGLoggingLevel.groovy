package io.opsguru.ci

/* OpsGuru LoggingLevel */
enum OGLoggingLevel {
    fatal(0x0),
    error(0x1),
    warn(0x2),
    info(0x4),
    debug(0x8),
    trace(0x10)

    private final int level;

    OGLoggingLevel(int level) {
        this.level = level
    }

}