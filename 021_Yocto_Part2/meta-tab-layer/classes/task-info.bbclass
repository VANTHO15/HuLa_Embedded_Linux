# task-info.bbclass


do_fetch_info() {
    bbplain "***********************************************"
    bbplain "*  Fetching ${PN}_${PV}   *"
    bbplain "***********************************************"
}

addtask do_fetch_info before do_fetch

do_unpack_info() {
    bbplain "***********************************************"
    bbplain "*  Unpacking ${PN}_${PV}   *"
    bbplain "***********************************************"
}

addtask do_unpack_info before do_unpack after do_fetch

do_patch_info() {
    bbplain "***********************************************"
    bbplain "*  Patching ${PN}_${PV}   *"
    bbplain "***********************************************"
}

addtask do_patch_info before do_patch after do_unpack

do_configure_info() {
    bbplain "***********************************************"
    bbplain "*  Configuring ${PN}_${PV}   *"
    bbplain "***********************************************"
}

addtask do_configure_info before do_configure after do_patch

do_compile_info() {
    bbplain "***********************************************"
    bbplain "*  Compiling ${PN}_${PV}   *"
    bbplain "***********************************************"
}

addtask do_compile_info before do_compile after do_configure

do_install_info() {
    bbplain "***********************************************"
    bbplain "*  Installing ${PN}_${PV}   *"
    bbplain "***********************************************"
}

addtask do_install_info before do_install after do_compile

do_build_info() {
    bbplain "***********************************************"
    bbplain "*  ${PN}_${PV} built successfully   *"
    bbplain "***********************************************"
}

addtask do_build_info before do_packagedata after do_package
