== Version Watch ==

1. Install JBDS several times in order to perform a comparison of the plugin/feature versions installed, and generate an HTML report.

  Put them all under some folder ${INSTALL_FOLDER}.

2. Run maven to produce the report.

  mvn clean test -DinstallationsDir="${INSTALL_FOLDER}" -Dvwatch.includeIUs=".*jboss.*" -Dvwatch.md5check

// TODO: JBIDE-19058 refactor this to report_detailed.html & mention report_summary.html (report.html) too
3. Open report in output.html in a browser to view.

