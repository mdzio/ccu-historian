#!/bin/bash

PATH_BASE="/opt/ccu-historian"
PATH_CONFIG="${PATH_BASE}/config"
FILE_CONFIG="${PATH_CONFIG}/ccu-historian.config"

log() {
    echo "$(date +"%Y-%m-%d %T")|INFO   |${1}"
}

log_sub() {
    echo "                           |${1}"
}

add_cfg() {
    echo "${1}" >>${FILE_CONFIG}
}

log "mdzio/ccu-historian ${VERSION}"

if [[ ! -d "${PATH_CONFIG}" ]]; then
    log "Creating config directory ..."
    mkdir -p "${PATH_CONFIG}"
fi

if [[ ! -f "${FILE_CONFIG}" ]]; then
    log "Creating config file ..."

    if [[ -z "${CONFIG_HOST_IP}" || -z "${CONFIG_CCU_TYPE}" || -z "${CONFIG_CCU_IP}" ]]; then
        log "Required environment variables are missing!"
        log_sub "Please specify CONFIG_HOST_IP, CONFIG_CCU_TYPE and CONFIG_CCU_IP."
        exit 1
    fi

    touch "${FILE_CONFIG}"

    add_cfg "database.dir='/database'"
    add_cfg "database.webAllowOthers=true"

    add_cfg "devices.device1.address='${CONFIG_CCU_IP}'"
    add_cfg "devices.device1.type=${CONFIG_CCU_TYPE}"

    if [ -n "${CONFIG_CCU_PLUGIN1_TYPE}" ]; then
        add_cfg "devices.device1.plugin1.type=${CONFIG_CCU_PLUGIN1_TYPE}"
    fi

    if [ -n "${CONFIG_CCU_PLUGIN2_TYPE}" ]; then
        add_cfg "devices.device1.plugin2.type=${CONFIG_CCU_PLUGIN2_TYPE}"
    fi

    if [ -n "${CONFIG_CCU_USERNAME}" ]; then
        add_cfg "devices.device1.username='${CONFIG_CCU_USERNAME}'"
    fi

    if [ -n "${CONFIG_CCU_PASSWORD}" ]; then
        add_cfg "devices.device1.password='${CONFIG_CCU_PASSWORD}'"
    fi

    add_cfg "devices.historianAddress='${CONFIG_HOST_IP}'"

    if [ -n "${CONFIG_HOST_BINRPCPORT}" ]; then
        add_cfg "devices.historianBinRpcPort=${CONFIG_HOST_BINRPCPORT}"
    fi

    if [ -n "${CONFIG_HOST_XMLRPCPORT}" ]; then
        add_cfg "devices.historianXmlRpcPort=${CONFIG_HOST_XMLRPCPORT}"
    fi

    add_cfg "webServer.historianAddress='${CONFIG_HOST_IP}'"
fi

if [ -n "${CONFIG_KEEP_MONTHS}" ]; then
    REF_DATE=$(date -d "-${CONFIG_KEEP_MONTHS} month" +%Y-%m-%d)

    log "Running database maintenance 'clean' (removes all data before ${REF_DATE}) ..."
    # shellcheck disable=SC2086
    java ${CONFIG_JAVA_OPTS} -jar "${PATH_BASE}/ccu-historian.jar" -config "${FILE_CONFIG}" -clean "${REF_DATE}"
fi

if [[ -n "${CONFIG_KEEP_MONTHS}" || "${CONFIG_MAINTENANCE}" == "true" ]]; then
    log "Running database maintenance 'recalc' ..."
    # shellcheck disable=SC2086
    java ${CONFIG_JAVA_OPTS} -jar "${PATH_BASE}/ccu-historian.jar" -config "${FILE_CONFIG}" -recalc

    log "Running database maintenance 'compact' ..."
    # shellcheck disable=SC2086
    java ${CONFIG_JAVA_OPTS} -jar "${PATH_BASE}/ccu-historian.jar" -config "${FILE_CONFIG}" -compact
fi

log "Starting CCU-Historian using the following config:"
log_sub "---"
while IFS="" read -r cfg || [ -n "$cfg" ]; do
    log_sub "${cfg}"
done <"${FILE_CONFIG}"
log_sub "---"

exec java ${CONFIG_JAVA_OPTS} -jar "${PATH_BASE}/ccu-historian.jar" -config "${FILE_CONFIG}"
