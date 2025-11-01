SUMMARY = "Copies echostatus script in /usr/bin"
DEESCRIPTION = "echostatus provides the colorcoded scheme to print the status on the terminal. Red for Error, Yellow for Warning, Cyan for Info and Green for Success"

inherit task-info

LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://echostatus"

S = "${WORKDIR}"

RDEPENDS:${PN}  = "bash"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/echostatus ${D}${bindir}/
}

