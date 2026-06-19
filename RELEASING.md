Releasing
=========

1. Change the version number in `gradle.properties`
2. Update `androidApp/src/main/play/release-notes/en-AU/default.txt` with the changes
3. `gradlew updateBaselineProfile`
4. Commit any changes.
5. `git tag -a vX.X.X -m "Release X.X.X"`
6. `git push --tags`
7. Test out the build in the alpha channel in [Google Play](https://play.google.com/apps/publish/).
