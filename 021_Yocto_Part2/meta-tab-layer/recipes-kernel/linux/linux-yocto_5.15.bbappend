FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


SRC_URI += "file://gpio-sysfs.cfg"
SRC_URI += "file://0001-tab-char-driver.patch"

















# SRC_URI += "file://defconfig"
# KCONFIG_MODE = "alldefconfig"

