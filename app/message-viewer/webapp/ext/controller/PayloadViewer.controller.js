sap.ui.define(['sap/ui/core/mvc/ControllerExtension', 'sap/ui/model/json/JSONModel', 'sap/m/MessageToast'], function (e, t, o) {
  'use strict';
  return e.extend('com.binlah.sap.btp.cpi.messageviewer.ext.controller.PayloadViewer', {
    metadata: { methods: {} },
    override: {
      onInit: function () {
        console.log('LoggingExtension initialized');
        const e = this.base.getExtensionAPI();
        console.log('oExtensionAPI:', e);
      },
      onAfterRendering: function () {
        console.log('LoggingExtension after rendering');
        var e = this.base.getExtensionAPI().getModel();
        console.log('oModel:', e);
      },
      onExit: function () {
        console.log('LoggingExtension destroyed');
      },
      routing: {
        onAfterBinding: async function (e) {
          console.log('LoggingExtension onAfterBinding');
          if (!e) return;
          const o = e.getPath();
          if (o === this._lastPath) return;
          this._lastPath = o;
          if (!this.blobModel) {
            this.blobModel = new t({ payload: '', mimeType: '' });
            this.base.getView().setModel(this.blobModel, 'blobModel');
          }
          await this._loadBlob();
        },
      },
    },
    _loadBlob: async function () {
      try {
        console.log('LoggingExtension _loadBlob');
        const oCtx = this.base.getView().getBindingContext();
        if (!oCtx) return;
        const oData = oCtx.getObject();
        const storeId = encodeURIComponent(oData.Id);

        // Get main OData V4 model
        const oModel = this.base.getExtensionAPI().getModel();
        console.log('oModel:', oModel);

        let sServiceUrl = oModel.getServiceUrl ? oModel.getServiceUrl() : oModel.sServiceUrl || '';
        console.log('sServiceUrl:', sServiceUrl);

        // sServiceUrl = sServiceUrl.replace(/^\.\//, ''); // remove leading "./" if any
        // sServiceUrl = '/' + sServiceUrl.replace(/^\/+/, ''); // force one leading "/"
        if (!sServiceUrl.endsWith('/')) {
          sServiceUrl += '/';
        }

        console.log('sServiceUrl:', sServiceUrl);

        // Build the full backend URL using the model's service URL
        const sUrl = `${sServiceUrl}MessageStoreEntries('${storeId}')/blob`;
        console.log('sUrl:', sUrl);

        const oResponse = await fetch(sUrl, { headers: { Accept: '*/*' }, credentials: 'include' });

        // const n = `odata/v4/remote/MessageStoreEntries('${storeId}')/blob`;
        // const s = await fetch(n, { headers: { Accept: '*/*' }, credentials: 'include' });

        if (!oResponse.ok) throw new Error(`${oResponse.status} ${oResponse.statusText}`);
        const i = (oResponse.headers.get('content-type') || '').toLowerCase();
        console.log('Fetched blob content-type:', i);
        let a = '';
        if (i.includes('application/json')) {
          const e = await oResponse.json();
          a = JSON.stringify(e, null, 2);
        } else if (i.includes('xml') || i.includes('text/')) {
          a = await oResponse.text();
        } else {
          const e = await oResponse.arrayBuffer();
          try {
            a = new TextDecoder('utf-8').decode(e);
          } catch (e) {
            a = '[binary content not displayable]';
          }
        }
        console.log('Fetched blob:', oResponse);
        console.log('Fetched blob content:', a);
        this.blobModel.setData({ payload: a, mimeType: i });
      } catch (e) {
        this.blobModel.setData({ payload: `[ERROR]\n${e.message || e}`, mimeType: '' });
      }
    },
  });
});
//# sourceMappingURL=PayloadViewer.controller.js.map
