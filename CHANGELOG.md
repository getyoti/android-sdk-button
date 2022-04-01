# Changelog
All notable changes to this project will be documented in this file.
## [1.3.2] - 2022-03-30
### Fixed
- Fix crash when returning from Digital IDs Apps.

## [1.3.1] - 2022-02-22
### Changed
- Update pending intent flags to support Android 12.

## [1.3.0] - 2021-07-20
### Added
- Added attribute buttonTheme to the YotiSDKButton for applying the theme.
- Added OnAppNotInstalledListener for listening to app not installed status.

### Changed
- Deprecated the OnYotiAppNotInstalledListener

## [1.2.0] - 2020-03-26
### Changed
Move some SDK configuration to gradle. This allows for creating skinned SDKs with different backends,
although this is not a publically supported feature.

## [1.1.3] - 2020-02-03
### Removed
Removed `android:allowBackup` from manifest.

## [1.1.2] - 2020-01-16
### Added
[(#11)](https://github.com/getyoti/android-sdk-button/pull/11) Added setter `setUseCaseId()` which enables developers to define the useCaseId programatically. Prior to the change, the Use Case ID could only be set statically in the layout file.

## [1.1.1]
### Changed
Update publication scripts and publish from github actions

## [1.1.0] - 2019-08-22
### Added
Support for Android Q
Small changes in the sample apps to improve error handling.

## [1.0.0] - 2018-09-20
### Added
- Adding callback to notify the 3rd party app that the call to Yoti has been done.

### Changed
- Moving initialisation of scenarios to the onCreate method of the app.
- Readme file improvements.
- Forcing the opening of the Yoti app when SDK button is pressed to avoid app chooser to be showed.
- Bringing 3rd party app to the foreground after Yoti share completion: Added in Sample app 2, comments in Readme file.

### Removed
- Removing the possibility of having a self-signed certificate when letting the SDK doing the call to the backend.

## [0.0.6] - 2018-04-10
### Added
- setOnYotiButtonClickListener method
- Changelog file

### Changed
- Deprecating setOnYotiScenarioListener method
- Using newest version of base-commons-ui that adds space between the image and the text of the Yoti button
- Sorting out issues in the gradle files.
- Small changes in the Readme file

### Removed
- Retrieve profile section from the README file

## [0.0.5] - 2015-12-03
### Added
- Sample app where callback is handled manually
- License file

### Changed
- "Yoti not installed" scenario can now be dealt with by the user

