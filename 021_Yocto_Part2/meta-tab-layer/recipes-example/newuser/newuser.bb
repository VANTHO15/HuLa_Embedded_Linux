# In your Linux terminal, execute the command to encrypt the password:  
# printf "%q" $(openssl passwd YOURPASSWORD) where you replace **YOURPASSWORD** with your desired 
# password. 
LICENSE = "CLOSED"
inherit useradd

PASSWD = "UMNet0gKxMJ5s"

NEWUSER = "tab"

USERADD_PACKAGES = "${PN}"

USERADD_PARAM:${PN} += " -d /home/${NEWUSER} -p '${PASSWD}' ${NEWUSER}"

do_install() {
    install -d ${D}/home/${NEWUSER}
}

FILES:${PN} += "/home/${NEWUSER}"