# Changelog
All notable changes to this project will be documented in this file.


## [1.0.0] - 2018-09-20
### Added
- Adding callback to notify the 3rd party app that the call to Yoti has been done.

### Changed
- Moving initialisation of scenarios to the onCreate method of the app.
- Readme file improvements.
- Forcing the opening of the Yoti app when SDK button is press to avoid app chooser to be showed.
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

