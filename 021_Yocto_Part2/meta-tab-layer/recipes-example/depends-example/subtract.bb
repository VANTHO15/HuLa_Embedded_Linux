SUMMARY = "Math library for basic arithmetic operations"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://math-lib-2.c \
           file://subtract.c "

S = "${WORKDIR}"

PROVIDES = "libmath"

do_compile() {
    ${CC} ${CFLAGS} -c math-lib-2.c -o math-lib-2.o
    ar rcs libmath.a math-lib-2.o

    ${CC} ${CFLAGS} -c subtract.c -o subtract_inter.o
    ${CC} ${CFLAGS} ${LDFLAGS} math-lib-2.o subtract_inter.o -o subtract -Wl,--hash-style=gnu

}

do_install() {
    install -d ${D}${libdir}
    install -m 0644 libmath.a ${D}${libdir}

    install -d ${D}${bindir}
    install -m 0755 subtract ${D}${bindir}
}

FILES:${PN} += "${libdir}/libmath.a"
