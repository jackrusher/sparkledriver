# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
### Changed
- `fetch!` now calls `str` on its argument, making it compatible with URL types like `java.net.URL` and `lambdaisland.uri.URI`.

## [0.1.9] - 2017-08-09
### Added
- Functions to return an element's `outer-html`.
- Tests for `inner-html` and `outer-html`.
### Changed
- Upgrade to jBrowserDriver `0.17.9`

## [0.1.8] - 2017-06-24
### Added
- Functions to evaluate JS in the context of the current page:
  `execute-script` and `execute-script-async`.
### Changed
- New all-local test harness no longer requires Internet access.

## [0.1.7] - 2017-06-19
### Added
- functions for interacting with `alert`s (`switch-to-alert`,
  `accept-alert`, `dismiss-alert`) and multiple windows
  (`current-window`, `all-windows`, `switch-to-window`,
  `maximize-window`).

## [0.1.6] - 2017-06-08
### Added
- `:screen-size [width height]` browser builder option to change the
  window size of the virtual browser.
### Changed
- jBrowserDriver version 0.17.8
- default browser window size is now 1366 x 768

## [0.1.5] - 2017-04-20
### Added
- page-wait
- attachments-dir
- media-dir
- cache-dir

## [0.1.4] - 2017-04-19
### Fixed
- terrible error where code wouldn't compile

## [0.1.3] - 2017-04-19
### Added
- Parameterized browser options
- Browser log access functions (wire tracing, for example)

### Changed
- jBrowserDriver version 0.17.7

## [0.1.2] - 2017-04-03
### Added
- Finished the wrapper for WebElement.

### Changed
- jBrowserDriver version 0.17.6.
- Improved docstrings

## [0.1.1] - 2016-11-04
- Initial release

[Unreleased]: https://github.com/your-name/sparkledriver/compare/0.1.2...HEAD
[0.1.8]: https://github.com/your-name/sparkledriver/compare/0.1.7...0.1.8
[0.1.7]: https://github.com/your-name/sparkledriver/compare/0.1.6...0.1.7
[0.1.6]: https://github.com/your-name/sparkledriver/compare/0.1.5...0.1.6
[0.1.5]: https://github.com/your-name/sparkledriver/compare/0.1.4...0.1.5
[0.1.4]: https://github.com/your-name/sparkledriver/compare/0.1.3...0.1.4
[0.1.3]: https://github.com/your-name/sparkledriver/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/your-name/sparkledriver/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/your-name/sparkledriver/compare/0.1.0...0.1.1
