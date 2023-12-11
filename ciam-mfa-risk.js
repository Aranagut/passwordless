var signalData = '';
var userAgentInfo = '';

function onPingOneSignalsReady(callback) {
	
	if (window['_pingOneSignalsReady']) {
		console.log('_pingOneSignalsReady');
		//callback();
	} else {
		document.addEventListener('PingOneSignalsReadyEvent', callback);
	}
}

onPingOneSignalsReady(function () {
	_pingOneSignals.init({
		// behavioralDataCollection: false
	}).then(function () {
		console.log("PingOne Signals initialized successfully");
	}).catch(function (e) {
		console.error("SDK Init failed", e);
	});
	
	_pingOneSignals.getData()
	   .then(function (result) {
		   signalData = result;
		   userAgentInfo = navigator.userAgent;
		   console.log("get data completed: " + signalData + '++++userAgentInfo: '+userAgentInfo);
	   }).catch(function (e) {
		   console.error('getData Error!', e);
	});
});

const script = document.createElement('script');

script.src = 'https://apps.pingone.com/signals/web-sdk/5.2.6/signals-sdk.js';

script.async = true;

script.onload = () => {
  console.log('Script loaded successfuly');
	onPingOneSignalsReady();
};

script.onerror = () => {
  console.log('Error occurred while loading script');
};

document.body.appendChild(script);
