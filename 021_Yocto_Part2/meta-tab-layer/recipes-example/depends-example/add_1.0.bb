SUMMARY = "Math library for basic arithmetic operations"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://math-lib.c \
           file://add_${PV}.c \
           file://Makefile "

S = "${WORKDIR}"

PROVIDES = "libmath"

do_compile() {
    # ${CC} ${CFLAGS} -c math-lib.c -o math-lib.o

    # ${CC} ${CFLAGS} -c add_${PV}.c -o add_inter.o
    # ${CC} ${CFLAGS} ${LDFLAGS} math-lib.o add_inter.o -o add -Wl,--hash-style=gnu
    oe_runmake
    ar rcs libmath.a math-lib.o
}

do_install() {
    install -d ${D}${libdir}
    install -m 0644 libmath.a ${D}${libdir}

    install -d ${D}${bindir}
    install -m 0755 add ${D}${bindir}
}

FILES:${PN} += "${libdir}/libmath.a"
