1.8

* Update reduction ratio checks to allow for ratios up-to 25.0x, including allowing the use of decimal numbers.

1.7 

* Add support for edition date checking with overlapping MFPak dates.

1.6

* Cache data read from database to avoid repeated queries to the database about the same information.

1.5

* Initalize checkers at beginning of run, to fail early on database errors, and avoid long open database connections.
* Update batch metadata checker
  * Require value of 0.0 in optical resolution when no ISO-FILM-target is present

1.4

* Use newest framework version
* Updated metadata- and batchcheckers
  * Improve section title selection
  * Section page number checking
  * Update check for ISO-FILM-target: Require the directory, but allow it to be empty
  * Update error message in mfpak date checks to be clearer
* Updated to newspaper-parent 1.2

1.3

* Updated to newspaper-parent 1.1, supporting new test strategy
* Improve the handling of failed Jpylyzer run in NinestarsFileQA
* Update structure-checker and metadata-checker
  * Only read SQL database information once
  * Support new empty pages change request
  * Update identifier scheme in agreement with Ninestars
  * Only require brik-files to be mentioned by at least one MODS file per image, not all of them

1.2

Use newest version of batch event framework, which improves performance in some areas, and adds functionality not used by this component
* Add support for fuzzy dates

Use newest md5 checker
* Use better error message on checksum errors

Use newest metadata checker

* Update MIX checks to be different on WORKSHIFT-ISO-TARGET matching 9*
* Disable check for special characters, it is not well defined
* No roundtrip in ALTO fileName
* Allow empty date in dateMicrofilmCreated
* Fix of fuzzy date pattern
* Fix NullPointerException in ModsXPathEventHandler when missing section title element
* Add support for fuzzy dates

1.1

Use newest version of checkers:

- Check for option B1/B2/B9 existence of ALTO files
- Check for Option B7 (reading of newspaper section titles)
- Bugfix: Allow more than one film per batch
- Update of film metadata schema to match discussions with Ninestars and to allow partial dates

1.0

Use newest versions of all checkers. See changelog for these for details.
Fix bug: qafile.sh did not run any tests

0.4

Use newer version of metadata checker

0.3

Use newest versions of all checkers. See changelog for these for details.

0.2

Slight update to documentation

0.1

Initial release
