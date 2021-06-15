# How to run tests

```sh
./gradlew clean test --tests <testScope> -Dconfig="<config-file>"
```
where `-Dconfig` is:
* `application-dev.yml` - for Develop stand
* `application-release.yml` - for Release stand
* `application-preprod.yml` - for Pre-prod stand

`testScope` should relate to a package with tests in `test/kotlin` package. Examples: 
* `--tests backend.sdex.smoke.*` - backend smoke 
* `--tests frontend.sdex.*` - frontend regression tests for sdex
* `--tests backend.kyx.*` - backend tests for KYX module
* `--tests frontend.sdex.login.*` - only for tests with Login component

Parameter `--tests` is repeated, for example `./gradlew clean test --tests backend.kyx.* --tests frontend.kyx.* -Dconfig="<config-file>"` will run both backend and frontend KYX module tests.


# Report
Application will return code 1 on unsuccessful runs. After run, all files required for report creation wil be located at `build/allure-results`. 
To generate report as HTML site, execute 
```sh
./gradlew allureReport
```

After successful execution report will be available at `build/reports/allure-report`.

To generate, host and open report in local browser, execute

```sh
./gradlew allureServe
```

# Tag

Use @Tag notations only from Junit5 by:

### Terminal

for single tag

```sh
gradle clean tags -Dconfig="<config-file>" -DskipBalances=true -DincludeTags='YourTagNameI' -DexcludeTags='YourTagNameX'
```

or for several

```sh
gradle clean tags -Dconfig="<config-file>" -DskipBalances=true -DincludeTags='YourTagNameA | YourTagNameB | YourTagNameC'
```

### Jenkins

* Copy testLauncherTemplate job
* Use parametrized launch with select Tag
* Select your branch and enter tags name or names **_without quotes and comma_**

###Rules for run with Tags
**_https://sdexnt.atlassian.net/wiki/spaces/ATM/pages/3360325767/Tag+usability_**
