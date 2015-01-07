#!/bin/bash

# script to run a jenkins job for vwatch

#################################################################

# configuration / defaults

# where is maven?
JAVA_HOME=/qa/tools/opt/jdk1.7.0_last
M2_HOME=/qa/tools/opt/apache-maven-3.1.1
PATH=$PATH:$M2_HOME/bin:$JAVA_HOME/bin
MVN=${M2_HOME}/bin/mvn

# default upstream job = devstudio.product_master, devstudio.product_8.0.luna, etc.
UPSTREAM_JOB=""

# CSV list of additional installer jars to use. Will also use list of installers in install.jbds.list.txt.
# If the target folder already exists, installation will be skipped.<br/>
#  eg., /qa/services/http/binaries/RHDS/builds/stable/8.0.0.GA-build-core/jboss-devstudio-8.0.0.GA-v20141020-1042-B317-installer-standalone.jar
JBDS_INSTALLERS=

# Location where JBDS installations will be put
INSTALL_FOLDER=/home/hudson/static_build_env/jbds/versionwatch/installations

# To generate a report containing fewer bundles/features, set a regex that will match only those you want in the report, eg., .*(hibernate|jboss|xulrunner).* or match everything with .*
FILTER=".*(hibernate|jboss|xulrunner).*"

FROM=${WORKSPACE}/sources/vwatch
STAGING=tools@filemgmt.jboss.org:/downloads_htdocs/tools/builds/staging/
DEST=${STAGING}/${JOB_NAME}
URL=http://download.jboss.org/jbosstools/builds/staging/${JOB_NAME}
DESCRIPTION=""

# file from which to pull a list of JBDS installers to install
JBDS_INSTALLERS_LISTFILE=${FROM}/install.jbds.list.txt

# include and exclude patterns for which JBDS installs to use when producing the version diff report
INCLUDE_VERSIONS="\d+\.\d+\.\d+"
EXCLUDE_VERSIONS=""

# read commandline args
while [[ "$#" -gt 0 ]]; do
  case $1 in
    '-JAVA_HOME') JAVA_HOME="$2"; shift 1;;
    '-M2_HOME') M2_HOME="$2"; shift 1;;
    '-UPSTREAM_JOB') UPSTREAM_JOB="$2"; shift 1;;
    '-JBDS_INSTALLERS') JBDS_INSTALLERS="$2"; shift 1;;
    '-INSTALL_FOLDER') INSTALL_FOLDER="$2"; shift 1;;
    '-JBDS_INSTALLER_NIGHTLY_FOLDER') JBDS_INSTALLER_NIGHTLY_FOLDER="$2"; shift 1;;
    '-JBDS_INSTALLERS_LISTFILE') JBDS_INSTALLERS_LISTFILE="$2"; shift 1;;
    '-FILTER') FILTER="$2"; shift 1;;
    '-INCLUDE_VERSIONS') INCLUDE_VERSIONS="$2"; shift 1;;
    '-EXCLUDE_VERSIONS') EXCLUDE_VERSIONS="$2"; shift 1;;
    *) others="$others,$1"; shift 0;;
  esac
  shift 1
done

# if not set commandline, use default upstream job based on this job's name -> devstudio.product_master, devstudio.product_8.0.luna, etc.
if [[ ! ${UPSTREAM_JOB} ]]; then
  if [[ ${JOB_NAME} ]]; then
    UPSTREAM_JOB=${JOB_NAME/versionwatch/product}
  else
    UPSTREAM_JOB=devstudio.product_master
  fi
fi

if [[ ! ${JBDS_INSTALLER_NIGHTLY_FOLDER} ]]; then
  # Folder from which to install the latest nightly JBDS build, and run the version watch comparing this latest against
  # the baseline JBDS_INSTALLERS. This will always overwrite if the version has changed since last time.
  JBDS_INSTALLER_NIGHTLY_FOLDER=/qa/services/http/binaries/RHDS/builds/staging/${UPSTREAM_JOB}/installer/
fi

# define globals in case they were overridden above
export JAVA_HOME
export M2_HOME
export PATH

# add more auditing/tests here as needed
check_results ()
{
  name=$1
  calltoaction=":: See ${URL}/results_B${BUILD_NUMBER}_${BUILD_ID}_${name}.html and ${URL}/report_B${BUILD_NUMBER}_${BUILD_ID}_${name}.html"
  if [[ ! `egrep -l "<td>|<tr>" ${FROM}/output_${name}.html` ]]; then
    echo "FAILURE IN OUTPUT: Empty results in output_${name}.html"
    echo $calltoaction
  fi
  if [[ `egrep -l "ERROR:" ${FROM}/output_${name}.html` ]]; then
    echo "FAILURE IN OUTPUT: Errors found in output_${name}.html"
    echo $calltoaction
  fi
}

publish ()
{
  name=$1
  # rename in workspace
  mv ${FROM}/output.html ${FROM}/output_${name}.html
  mv ${FROM}/product.html ${FROM}/product_${name}.html
  # publish
  echo "mkdir ${JOB_NAME}" | sftp ${STAGING}
  rsync -Pzrlt --rsh=ssh --protocol=28 ${FROM}/output_${name}.html ${DEST}/results_B${BUILD_NUMBER}_${BUILD_ID}_${name}.html
  rsync -Pzrlt --rsh=ssh --protocol=28 ${FROM}/product_${name}.html ${DEST}/report_B${BUILD_NUMBER}_${BUILD_ID}_${name}.html
  # create links to html files (must be all on one line)
  DESCRIPTION="${DESCRIPTION}"'<li><a href="'${URL}'/results_B'${BUILD_NUMBER}'_'${BUILD_ID}'_'${name}'.html">Results</a>, <a href="'${URL}'/report_B'${BUILD_NUMBER}'_'${BUILD_ID}'_'${name}'.html">Report</a> for /.*/</li>'
}

#################################################################

# do JBDS installs so we can compare them
pushd ${FROM}
. ${FROM}/install.jbds.sh -JBDS_INSTALLERS_LISTFILE ${JBDS_INSTALLERS_LISTFILE}
popd

# generate reports and publish them
pushd ${WORKSPACE}
${MVN} -f ${FROM}/pom.xml clean test -Dmaven.repo.local=${WORKSPACE}/.repository -DexcludeVersions="${EXCLUDE_VERSIONS}" -DincludeVersions="${INCLUDE_VERSIONS}" \
-DinstallationsDir="${INSTALL_FOLDER}" -Dfilter="${FILTER}" && publish filtered && check_results filtered
${MVN} -f ${FROM}/pom.xml clean test -Dmaven.repo.local=${WORKSPACE}/.repository -DexcludeVersions="${EXCLUDE_VERSIONS}" -DincludeVersions="${INCLUDE_VERSIONS}" \
-DinstallationsDir="${INSTALL_FOLDER}" -Dfilter=".*" && publish all && check_results all
popd

# set build description (Jenkins only sees the first occurrence)
BUILD_DESCRIPTION="${DESCRIPTION}"
