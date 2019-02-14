# Changelog

## Unreleased

### Added
- **@exception** token support for methods. Credit goes to [@mlockett](https://github.com/mlockett) for his [pull request](https://github.com/SalesforceFoundation/ApexDoc/pull/75) to the original ApexDoc. Provide a list of exceptions or a description of the exceptions a method might throw.
- **@deprecated** token support for classes and methods. Credit goes to [@mlockett](https://github.com/mlockett) for his [pull request](https://github.com/SalesforceFoundation/ApexDoc/pull/75) to the original ApexDoc. Value for this token should be reasoning or what the method or class was replaced with. For methods, highlights name in red to draw attention.
- **@see** token support for classes and methods. Provide ApexDoc2 with a fully qualified class or method name with this token, and a link to that class or method in the documentation will be created. The name must be a fully qualified name, even if its a reference to another method in the same class, e.g. Class.Method, Class.InnerClass, Class.InnerClass.InnerClassMethod. If a matching class or method cannot be found, the text will not be wrapped in an `<a>` and it will be given a tooltip which says: 'A matching reference cannot be found!'. This works for classes, methods, inner classes and methods on inner classes. Properties cannot be linked to at this time.
- Added support for inline code in most tokens. Wrap a word or words in backticks and they will be formatted as code in the output documentation. Useful for single line snippets and for drawing attention to keywords, etc. Multi-line code examples should still go in the **@example** token. E.g.:

```
/**
 * @description The following will be formatted as code: `String cool = 'cool!';`
 */
 public static String exampleMethod() {
```
- Added optional **<toc_description>** command line argument. As a matter of preference, if you find the method description snippet in the class's table of contents distracting, you can now hide it with this argument. Defaults to `false`.
- Added optional **<sort_order>** command line argument. This controls the order in which methods, properties, and inner classes are presented in the output documentation. Either 'logical', the order they appear in the source file, or 'alpha', alphabetically. Defaults to the ApexDoc original of alphabetical order.
- Added optional **<document_title>** command line argument. Allows you to set the value of the HTML document's `<title>` attribute. Now defaults to 'ApexDocs' instead of 'index.html'.
- Added support for `//` style comments inside of **@example** code snippets in case some explanation of the code is needed, it will appear properly commented out in the output docs.
- Added support for empty line preservation inside of **@example** code snippets where spacing might be needed for complex code examples.
- Changed logo to ApexDoc2 logo, added favicon.

### Changed
- Reordered output of **@author**, **@date**, and **@example** tokens so that example snippets always come last for better UI.
- When **@group-content** token is used, no longer link to class group html page on `<a>` tag click. Instead, use SVG info icon so that user can expand menu without navigating to class group content page.
- Minified JQuery for 31.4KB gain.

### Fixed
- Fixed CSS bug for TOC method descriptions: `text-overflow: ellipsis;` was not working as `white-space: nowrap;` was missing. Also made the width of the descriptions smaller, as they were extending across the whole page which I found a bit distracting. Now will have ellipsis overflow at 500px;
- Fixed line-height CSS for TOC method descriptions. The bottom of letters like 'g' and '__' were getting cut off, now full line is visible.
- Fix comparisons when getting token content so that tokens without values do not get rendered.