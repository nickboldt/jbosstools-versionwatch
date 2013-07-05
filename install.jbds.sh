#!/bin/bash

# Purpose: run this script in in a Jenkins job to install multiple versions of JBDS, then compare the versions 
# of plugins/features in those installations using https://github.com/jbdevstudio/jbdevstudio-qa/tree/master/vwatch

# If you want to use this script locally, you need to set some overrides - see commandline args below

# Usage: wget this script from raw.github, then call it from an "Execute shell" step in your job, before calling 
# mvn -f vwatch/pom.xml clean test -DinstallationsDir="${INSTALL_FOLDER}" -Dfilter=".*jboss.*" -Dvwatch.md5check

# Jenkins variables:

# INSTALL_FOLDER :: Location where JBDS installations will be put; default: /home/hudson/static_build_env/jbds/versionwatch/installations
INSTALL_FOLDER=/home/hudson/static_build_env/jbds/versionwatch/installations

# JBDS_INSTALLER_NIGHTLY_FOLDER :: /qa/services/http/binaries/RHDS/builds/staging/devstudio.product_70/installer/
# Folder from which to install the latest nightly JBDS build, and run the version watch comparing this latest against
# the baseline JBDS_INSTALLERS. This will always overwrite if the version has changed since last time.

# JBDS_INSTALLERS :: CSV list of additional installer jars to use. Will also use list of installers in JBDS_INSTALLERS_LISTFILE = install.jbds.list.txt. 
# If the target folder already exists, installation will be skipped. 
# /qa/services/http/binaries/RHDS/builds/development/7.0.0.Beta2.installer/jbdevstudio-product-universal-7.0.0.Beta2-v20130626-0242-B345.jar, 
# /qa/services/http/binaries/RHDS/builds/development/7.0.0.Beta1.installer/jbdevstudio-product-universal-7.0.0.Beta1-v20130529-0631-B257.jar 

# JBDS_INSTALLERS_LISTFILE :: used to find install.jbds.list.txt; use either present working directory or passed in commandline arg
JBDS_INSTALLERS_LISTFILE=`pwd`/install.jbds.list.txt

# BASE_URL :: if path to installer is not found locally, set a base URL instead from which to download them; default: http://www.qa.jboss.com/binaries/RHDS/
BASE_URL=http://www.qa.jboss.com/binaries/RHDS

usage() {
  echo "$0"
  echo "  [ -JAVA /qa/tools/opt/jdk1.6.0_last/bin/java ]"
  echo "  [ -BASE_URL http://www.qa.jboss.com/binaries/RHDS ]" 
  echo "  [ -INSTALL_FOLDER /home/hudson/static_build_env/jbds/versionwatch/installations"
  echo "  [ -JBDS_INSTALLER_NIGHTLY_FOLDER /path/to/builds/staging/devstudio.product_70/installer/"
  echo "  [ -JBDS_INSTALLERS_LISTFILE /path/to/install.jbds.list.txt"
  echo "  [ -JBDS_INSTALLERS /path/to/jbdevstudio-product-universal-7.0.0.Beta2-v20130626-0242-B345.jar, /path/to/jbdevstudio-product-universal-7.0.0.Beta1-v20130529-0631-B257.jar"
}

if [[ $# -eq 0 ]]; then
  usage; exit
fi

# read commandline args
while [[ "$#" -gt 0 ]]; do
  case $1 in
    '-JAVA') JAVA="$2"; shift 1;; # /path/to/bin/java
    '-BASE_URL') BASE_URL="$2"; shift 1;; # if path to installer is not found locally, set a base URL instead from which to download; default: http://www.qa.jboss.com/binaries/RHDS
    '-INSTALL_FOLDER') INSTALL_FOLDER="$2"; shift 1;; # path to parent folder under which to perform installations, eg., /home/hudson/static_build_env/jbds/versionwatch/installations
    '-JBDS_INSTALLER_NIGHTLY_FOLDER') JBDS_INSTALLER_NIGHTLY_FOLDER="$2"; shift 1;; # Folder from which to install the latest nightly JBDS build
    '-JBDS_INSTALLERS') JBDS_INSTALLERS="$2"; shift 1;; 
    '-JBDS_INSTALLERS_LISTFILE') JBDS_INSTALLERS_LISTFILE="$2"; shift 1;; # path to install.jbds.list.txt or other file with CSV or one-per-line list of JBDS installers to run
    esac
  shift 1
done

# which version of Java are we using? 
if [[ ${JAVA} ]]; then
  if [[ ! -x ${JAVA} ]]; then 
    echo "Could not execute ${JAVA}! Please use -JAVA /path/to/bin/java"; exit 1
  fi
else
  if [[ -x /qa/tools/opt/jdk1.6.0_last/bin/java ]]; then 
    JAVA=/qa/tools/opt/jdk1.6.0_last/bin/java
  elif [[ -x /usr/bin/java ]]; then
    JAVA=/usr/bin/java
  elif [[ -x /bin/java ]]; then
    JAVA=/bin/java
  else
    echo "Could not find path to /bin/java! Please use -JAVA /path/to/bin/java"; exit 1
  fi
fi
${JAVA} -version

# get list of installers to install
INSTALLER_LIST="${JBDS_INSTALLERS} `if [[ ${JBDS_INSTALLERS_LISTFILE} ]] && [[ -f ${JBDS_INSTALLERS_LISTFILE} ]]; then cat ${JBDS_INSTALLERS_LISTFILE}; fi`"
if [[ ! ${INSTALLER_LIST} ]]; then 
  echo "No installers defined! Must specify installers to use as baseline for comparison using"
  echo "  -JBDS_INSTALLERS /path/to/installer.jar,/path/to/installer2.jar"
  echo "    and/or"
  echo "  -JBDS_INSTALLERS_LISTFILE /path/to/listfile.txt"
  exit 1;
fi

# location for downloaded installers
TMPDIR=/tmp

# define install config file
installJBDS() {
  version=${1}
  jar=${2}
  echo "<?xml version='1.0' encoding='UTF-8' standalone='no'?>
<AutomatedInstallation langpack='eng'>
<com.jboss.jbds.installer.HTMLInfoPanelWithRootWarning id='introduction'/>
<com.izforge.izpack.panels.HTMLLicencePanel id='licence'/>
<com.jboss.jbds.installer.PathInputPanel id='target'>
<installpath>${INSTALL_FOLDER}/jbds-${version}</installpath>
</com.jboss.jbds.installer.PathInputPanel>
<com.jboss.jbds.installer.JREPathPanel id='jre'/>
<com.jboss.jbds.installer.JBossAsSelectPanel id='as'>
<installgroup>jbds</installgroup>
</com.jboss.jbds.installer.JBossAsSelectPanel>
<com.jboss.jbds.installer.UpdatePacksPanel id='updatepacks'/>
<com.jboss.jbds.installer.DiskSpaceCheckPanel id='diskspacecheck'/>
<com.izforge.izpack.panels.SummaryPanel id='summary'/>
<com.izforge.izpack.panels.InstallPanel id='install'/>
<com.jboss.jbds.installer.CreateLinkPanel id='createlink'>
<jrelocation>$(which java)</jrelocation>
</com.jboss.jbds.installer.CreateLinkPanel>
<com.izforge.izpack.panels.ShortcutPanel id='shortcut'/>
<com.jboss.jbds.installer.ShortcutPanelPatch id='shortcutpatch'/>
<com.izforge.izpack.panels.SimpleFinishPanel id='finish'/>
</AutomatedInstallation>" > ${INSTALL_FOLDER}/jbds-${version}.install.xml
  if [[ ! -f ${jar} ]] && [[ ${BASE_URL} ]]; then
    # download the installer 
    remoteJar=${BASE_URL}/${jar##/qa/services/http/binaries/RHDS/}
    jar=${TMPDIR}/${jar##*/}
    echo "${jar} not found, so download it from ${remoteJar}"
    pushd ${TMPDIR}/ >/dev/null; wget -nc ${remoteJar}; popd >/dev/null
  fi
  ${JAVA} -jar ${jar} ${INSTALL_FOLDER}/jbds-${version}.install.xml
}

# install the latest nightly, caching the last version used in jbds-7.0.0.CR1/version.txt so we only ever have one nightly at a time
for i in `find ${JBDS_INSTALLER_NIGHTLY_FOLDER} -name "jbdevstudio-product-universal-*.jar"`; do
  ver=${i##*-universal-}; ver=${ver%%.jar}; # 7.0.0.Beta2-v20130626-0242-B345
  f=${i##*-universal-}; f=${f%%-*}; # 7.0.0.Beta2
  LATEST=${INSTALL_FOLDER}/jbds-${f}/version.txt
  if [[ -d ${INSTALL_FOLDER}/jbds-${f} ]] && [[ -f ${LATEST} ]] && [[ `cat ${LATEST}` == $ver ]]; then 
    echo "Existing JBDS install in ${INSTALL_FOLDER}/jbds-${f} (${ver})"
  else
    # wipe existing installation
    if [[ ${f} ]] && [[ -d ${INSTALL_FOLDER}/jbds-${f} ]]; then rm -fr ${INSTALL_FOLDER}/jbds-${f}; fi
    # echo "Install JBDS ${f} (${ver}) to ${INSTALL_FOLDER}/jbds-${f} ..."
    installJBDS ${f} ${i}
    echo "${ver}" > ${LATEST}
  fi
done

# install stable releases + development milestones (baselines for comparison)
for i in ${INSTALLER_LIST}; do
  # if target folder does not exist, run the installer
  ver=${i##*jbdevstudio-product-linux-gtk-}; ver=${ver##*-universal-}; ver=${ver%%.jar*}; # 7.0.0.Beta2-v20130626-0242-B345
  # f=${i##*-universal-}; f=${f%%-*}; # 7.0.0.Beta2
  if [[ -d ${INSTALL_FOLDER}/jbds-${ver} ]]; then 
    echo "Existing JBDS install in ${INSTALL_FOLDER}/jbds-${ver}"
  else
    # echo "Install JBDS ${ver} to ${INSTALL_FOLDER}/jbds-${ver} ..."
    installJBDS ${ver} ${i%%,}
  fi
done

echo "Now run this:"
echo ""
echo "mvn clean test -DinstallationsDir=${INSTALL_FOLDER} -Dfilter=\".*jboss.*\" -Dvwatch.md5check"
