sap.ui.define([], function () {
  'use strict';

  return {
    onBeautifyPressed: function (oEvent) {
      // Start from the button, walk up to the VBox that contains the editor
      var oToolbar = oEvent.getSource().getParent();
      var oBox = oToolbar.getParent();

      // Find the CodeEditor among VBox items
      var oEditor = oBox.getItems().find(function (c) {
        return c && c.isA && c.isA('sap.ui.codeeditor.CodeEditor');
      });

      // Fallback by global ID resolution in case layout changes
      if (!oEditor) {
        var sEditorId = oBox.getId() + '--payloadEditor';
        oEditor = sap.ui.getCore().byId(sEditorId);
        console.log('oEditor:' + oEditor);
      }

      if (oEditor && oEditor.prettyPrint) {
        // Optional: log to verify the handler is bound
        /* eslint-disable no-console */
        console.log('Beautify pressed; pretty-printing payload.');
        oEditor.prettyPrint();
      } else {
        console.warn('CodeEditor not found or prettyPrint() not available.');
      }
    },

    onDownloadPressed: function (oEvent) {
      const src = oEvent.getSource();

      // 1) Read payload directly from the model (same data shown in CodeEditor)
      const m = src.getModel('blobModel');
      const payload = m?.getProperty('/payload') ?? '';
      if (!payload) {
        MessageToast.show('Nothing to download');
        return;
      }

      // 2) Build the filename from the OP context (MessageStoreId)
      const data = src.getBindingContext()?.getObject?.();
      const base = data?.MessageStoreId || 'payload';

      // Optional: pick extension based on mime/editor type
      const mimeOrType = (m?.getProperty('/mimeType') || m?.getProperty('/editorType') || m?.getProperty('/contentType') || '').toLowerCase();
      console.log('mimeOrType:', mimeOrType);
      const ext = mimeOrType.includes('json') ? 'json' : mimeOrType.includes('xml') ? 'xml' : 'txt';

      const fileName = `${base}.${ext}`;

      // 3) Trigger browser download
      const blob = new Blob([payload], { type: 'text/plain;charset=utf-8' });
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(a.href);
    },
  };
});
