SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE = "MIT"

python do_display_banner() {
    bb.plain("***********************************************");
    bb.plain("*                                             *");
    bb.plain("*  Example recipe created by bitbake-layers   *");
    bb.plain("*                                             *");
    bb.plain("***********************************************");
}

addtask display_banner before do_build

#  Variable Assignment
## Types of Variable Assignments

# COMMAND
# bitbake -e <RECIPE_NAME> | grep ^<VARIABLE_NAME>=

# ?=   : This is used to assign the default value to varibale. It can be overridden.

# TEST ?= "foo"
# TEST ?= "bar"
# TEST ?= "val"
# TEST ?= "var"
# The final value is TEST="foo" 
#------------------------------------------------------------------------------------

# ??= : This is used to assign the default value to varibale. But it is a weak assignment. It can be overridden. If multiple assignments are done with this type, the the last one will be considered.

# TEST ??= "foo"
# TEST ??= "bar"
# TEST ??= "val"
# TEST ??= "var"
# The final value is TEST="var"

# TEST ??= "foo"
# TEST ?= "bar"
# TEST ?= "val"
# TEST ??= "var"
# The final value is TEST="bar" 
#------------------------------------------------------------------------------------- 

# =   : This is a simple variable assignment. It requires "" and spaces are significant. But variables are expanded at the end.

# Override
# A ?= "foo"
# A = "bar"
# The final value is A="bar" 

# A = "foo"
# B = "${A}"
# A = "bar"
# The final value is B="bar" 
#----------------------------------------------------------------------------------------

# := : This is an immediate variable expansion. The value assigned is expanded immediately.

# Override
# A ?= "foo"
# A := "bar"
# The final value is A="bar" 

# Variable Expansion
# A = "foo"
# B := "${A}"
# A = "bar"

#-----------------------------------------------------------------------------------------

# += : This appends a value to a variable. The operator inserts **a space** between the current value and appended value. It takes effect immediately.

# Append
# A = "foo"
# A += "bar"
# The final value is A="foo bar" 

# Append
# A ?= "val"
# A += "var"
# The final value is A="var"

#------------------------------------------------------------------------------------

# =+ : This prepends a value to a variable, The operator inserts **a space** between the current value and prepended value. It takes effect immediately.

# Prepend
# B = "foo"
# B =+ "bar"
# The final value is B="bar foo"

# Prepend
# B ??= "val"
# B =+ "var"
# The final value is B="var"

#---------------------------------------------------------------------------------------------------

# .= :  This appends a value to a variable. The operator inserts **no space** between the current value and appended value. It takes effect immediately.

# No Spaces are added here
# Needs to add extra space

# Append
# A = "foo"
# A .= "bar"
#-----------------------------------------------------------------------------------

# =. : This prepends a value to a variable. The operator inserts **no space** between the current value and prepended value. It takes effect immediately.

# Prepend
# B = "foo"
# B =. "bar "
#-------------------------------------------------------------------------------------------

# :append :  This appends a value to a variable. The operator inserts **no space** between the current value and appended value.  The effects are applied at variable expansion time rather than being immediately applied.

# Append
# A = "foo"
# A:append = " bar"
# The final value is A="foobar" 

# Append
# A = "foo"
# A:append = "bar"
# A += "val"
# The final value is A="foo valbar" 
#------------------------------------------------------------------------------------------------

# :prepend :  This appends a value to a variable. The operator inserts **no space** between the current value and appended value. The effects are applied at variable expansion time rather than being immediately applied.

# Prepend
# A = "foo"
# A:prepend = "bar"
# The final value is A="barfoo" 

# Prepend
# A = "foo"
# A:prepend = "bar"
# A =+ "val"
# The final value is A="barval foo" 
#------------------------------------------------------------------------------------------------

# :remove : This remove values from lists. Specifying a value for removal causes all occurrences of that value to be removed from the variable.

# A = "foo bar foo bar foo foo"
# A:remove = "bar"
# The final value is A=" bar" 

A = "foo bar"
A:remove = "var"
A += "var"
# The final value is A=" foo bar val" 
#---------------------------------------------------------------------------------------------
