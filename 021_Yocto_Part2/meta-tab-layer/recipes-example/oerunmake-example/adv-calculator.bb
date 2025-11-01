SUMMARY = "Simple calculator application"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://adv-calculator.c \
           file://lib-add.c \
           file://lib-subtract.c \
           file://lib-multiply.c \
           file://Makefile \
           "

S = "${WORKDIR}"

LIBS = "lib-add.c\ lib-subtract.c\ lib-multiply.c"

EXTRA_OEMAKE:append = " DESTDIR=${D} BINDIR=${bindir}"
EXTRA_OEMAKE:append = " TARGET=${PN} libs=${LIBS}"


do_compile() {
   oe_runmake 
}

do_install() {
    oe_runmake install 
}









# LIBS = "lib-add.c\ lib-subtract.c\ lib-multiply.c" 
# EXTRA_OEMAKE:append = " TARGET=${PN} libs=${LIBS}"