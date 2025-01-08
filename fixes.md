The following fixes are needed:

1. Remove duplicate methods in ReportSubmissionActivity.java:
   - onActivityResult
   - processSelectedImage
   - updateAttachmentStatus

2. Fix Report class method usage:
   - setLocation expects Location object, not String
   - addMediaUrl method exists and is correct

3. Fix variable redefinition:
   - reportDetails variable in submitReport method

4. Fix ReportPage adapter initialization:
   - Incorrect type being passed to adapter constructor