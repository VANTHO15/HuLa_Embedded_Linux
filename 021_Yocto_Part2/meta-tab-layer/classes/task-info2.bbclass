inherit task-info

do_between_unpack_and_patch() {
    bbplain "***********************************************"
    bbplain "*  I AM BETWEEN UNPACK AND PATCH   *"
    bbplain "***********************************************"
}

addtask between_unpack_and_patch before do_patch_info after do_unpack_info