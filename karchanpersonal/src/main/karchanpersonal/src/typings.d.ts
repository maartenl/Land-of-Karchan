// Empty typings for the editor used in the app to satisfy the TS compiler in the strict mode:
declare module '@ckeditor/ckeditor5-build-classic' { // or other CKEditor 5 build.
	const ClassicEditorBuild: any;

	export = ClassicEditorBuild;
}
