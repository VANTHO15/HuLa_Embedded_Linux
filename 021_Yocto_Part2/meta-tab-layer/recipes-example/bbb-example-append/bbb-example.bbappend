FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://0001-patch-example.patch"
do_configure:append() {
    echo "#define GOODBYE y" >> bbb-example.h
}
















