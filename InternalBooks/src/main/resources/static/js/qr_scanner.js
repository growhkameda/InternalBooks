/**
 * QRコードスキャナー - モバイル対応シンプル版
 * jsQRライブラリを使用してQRコードの読み取りを行う
 * iPhone/Android端末での使用を想定
 */

// ===== グローバル変数の定義 =====
let video;          // ビデオ要素（カメラ映像表示用）
let canvas;         // キャンバス要素（QRコード解析用）
let context;        // キャンバスの2D描画コンテキスト
let scanning = false;   // スキャン中フラグ
let stream = null;      // カメラストリーム（停止時に使用）

/**
 * ページ読み込み完了時に実行される初期化処理
 */
document.addEventListener('DOMContentLoaded', function() {
    // DOM要素の取得
    initializeElements();
    
    // イベントリスナーの設定
    setupEventListeners();
    
    // カメラ対応チェック
    checkCameraSupport();
});

/**
 * DOM要素を取得して変数に格納
 */
function initializeElements() {
    video = document.getElementById('qr-video');
    canvas = document.getElementById('qr-canvas');
    
    // デバッグ：要素の存在確認
    console.log('Video要素:', video);
    console.log('Canvas要素:', canvas);
    
    // キャンバスが存在する場合、2D描画コンテキストを取得
    if (canvas) {
        context = canvas.getContext('2d');
        console.log('Canvas context:', context);
    } else {
        console.error('Canvasが見つかりません');
    }
    
    if (!video) {
        console.error('Video要素が見つかりません');
    }
}

/**
 * イベントリスナーの設定
 */
function setupEventListeners() {
    // カメラ開始ボタンのクリックイベント
    const startBtn = document.getElementById('start-scan-btn');
    if (startBtn) {
        startBtn.addEventListener('click', startScanning);
        console.log('スタートボタンのイベントリスナーを設定しました');
    } else {
        console.error('スタートボタンが見つかりません');
    }
    
    // カメラ停止ボタンのクリックイベント
    const stopBtn = document.getElementById('stop-scan-btn');
    if (stopBtn) {
        stopBtn.addEventListener('click', stopScanning);
        console.log('ストップボタンのイベントリスナーを設定しました');
    } else {
        console.error('ストップボタンが見つかりません');
    }
}

/**
 * カメラ対応チェック
 * ブラウザがカメラ機能をサポートしているかを確認
 */
function checkCameraSupport() {
    console.log('カメラ対応チェックを開始します...');
    
    // MediaDevices APIの対応チェック
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        console.error('MediaDevices APIがサポートされていません');
        showError('このブラウザはカメラ機能をサポートしていません');
        return false;
    }
    
    // jsQRライブラリの読み込みチェック
    if (typeof jsQR === 'undefined') {
        console.error('jsQRライブラリが読み込まれていません');
        showError('jsQRライブラリが読み込まれていません');
        return false;
    }
    
    console.log('カメラ対応チェック完了：すべて正常です');
    return true;
}

/**
 * QRコードスキャン開始処理
 * カメラを起動してスキャンを開始
 */
async function startScanning() {
    try {
        // すでにスキャン中の場合は処理しない
        if (scanning) {
            return;
        }
        
        // エラー表示をクリア
        hideError();
        hideResult();
        
        // カメラの制約設定（モバイル端末を考慮）
        const constraints = {
            video: {
                // モバイル端末では背面カメラを優先
                facingMode: { ideal: 'environment' },
                // 解像度設定（モバイル端末の性能を考慮）
                width: { ideal: 640, max: 1280 },
                height: { ideal: 480, max: 720 }
            }
        };
        
        // カメラストリームを取得
        stream = await navigator.mediaDevices.getUserMedia(constraints);
        
        // ビデオ要素にストリームを設定
        video.srcObject = stream;
        video.style.display = 'block';
        
        // ビデオの再生開始を待つ
        await new Promise((resolve) => {
            video.onloadedmetadata = () => {
                video.play();
                resolve();
            };
        });
        
        // キャンバスサイズをビデオサイズに合わせる
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        
        // スキャンフラグをON
        scanning = true;
        
        // ボタンの状態を更新
        updateButtonStates(true);
        
        // QRコードスキャンループを開始
        requestAnimationFrame(scanLoop);
        
    } catch (error) {
        console.error('カメラ開始エラー:', error);
        
        // エラーの種類に応じてメッセージを変更
        let errorMessage = 'カメラの開始に失敗しました';
        
        if (error.name === 'NotAllowedError') {
            errorMessage = 'カメラの使用が許可されていません。ブラウザの設定を確認してください';
        } else if (error.name === 'NotFoundError') {
            errorMessage = 'カメラが見つかりません';
        } else if (error.name === 'NotReadableError') {
            errorMessage = 'カメラが他のアプリケーションで使用中です';
        }
        
        showError(errorMessage);
    }
}

/**
 * QRコードスキャン停止処理
 * カメラを停止してリソースを解放
 */
function stopScanning() {
    // スキャンフラグをOFF
    scanning = false;
    
    // カメラストリームを停止
    if (stream) {
        stream.getTracks().forEach(track => track.stop());
        stream = null;
    }
    
    // ビデオ要素をクリア
    if (video) {
        video.srcObject = null;
        video.style.display = 'none';
    }
    
    // ボタンの状態を更新
    updateButtonStates(false);
    
    // 表示をクリア
    hideError();
    hideResult();
}

/**
 * QRコードスキャンのメインループ
 * 連続的にフレームを解析してQRコードを検出
 */
function scanLoop() {
    // スキャンが停止された場合は終了
    if (!scanning) {
        return;
    }
    
    // ビデオフレームをキャンバスに描画
    if (video.readyState === video.HAVE_ENOUGH_DATA) {
        context.drawImage(video, 0, 0, canvas.width, canvas.height);
        
        // キャンバスから画像データを取得
        const imageData = context.getImageData(0, 0, canvas.width, canvas.height);
        
        // jsQRでQRコードを解析
        const qrCode = jsQR(imageData.data, imageData.width, imageData.height);
        
        // QRコードが検出された場合
        if (qrCode) {
            console.log('QRコード検出:', qrCode.data);
            
            // スキャンを停止
            stopScanning();
            
            // 結果を表示
            showResult(qrCode.data);
            
            // QRコード検出時の処理を実行
            onQRCodeDetected(qrCode.data);
            
            return;
        }
    }
    
    // 次のフレームを処理（約60FPS）
    requestAnimationFrame(scanLoop);
}

/**
 * QRコード検出時の処理
 * @param {string} qrData - 検出されたQRコードのデータ
 */
function onQRCodeDetected(qrData) {
    // ここにQRコード検出時の具体的な処理を追加
    console.log('QRコードが読み取られました:', qrData);
    
    // 例：URLの場合は自動的に開く
    if (qrData.startsWith('http://') || qrData.startsWith('https://')) {
        if (confirm('このURLを開きますか？\n' + qrData)) {
            window.open(qrData, '_blank');
        }
    }
    
    // 例：書籍IDの場合は検索処理を実行
    // if (qrData.match(/^BOOK_\d+$/)) {
    //     searchBook(qrData);
    // }
}

/**
 * ボタンの状態を更新
 * @param {boolean} isScanning - スキャン中かどうか
 */
function updateButtonStates(isScanning) {
    const startBtn = document.getElementById('start-scan-btn');
    const stopBtn = document.getElementById('stop-scan-btn');
    
    if (startBtn) {
        startBtn.disabled = isScanning;
        if (isScanning) {
            startBtn.innerHTML = '<i class="bi bi-camera me-2"></i>スキャン中...';
        } else {
            startBtn.innerHTML = '<i class="bi bi-camera me-2"></i>スキャン開始';
        }
    }
    
    if (stopBtn) {
        stopBtn.disabled = !isScanning;
    }
}

/**
 * エラーメッセージを表示
 * @param {string} message - エラーメッセージ
 */
function showError(message) {
    const errorDiv = document.getElementById('error-message');
    const errorText = document.getElementById('error-text');
    
    if (errorDiv && errorText) {
        errorText.textContent = message;
        errorDiv.style.display = 'block';
    }
}

/**
 * エラーメッセージを非表示
 */
function hideError() {
    const errorDiv = document.getElementById('error-message');
    if (errorDiv) {
        errorDiv.style.display = 'none';
    }
}

/**
 * 結果メッセージを表示
 * @param {string} result - QRコードの内容
 */
function showResult(result) {
    const resultDiv = document.getElementById('result-message');
    const resultText = document.getElementById('result-text');
    
    if (resultDiv && resultText) {
        resultText.textContent = result;
        resultDiv.style.display = 'block';
    }
}

/**
 * 結果メッセージを非表示
 */
function hideResult() {
    const resultDiv = document.getElementById('result-message');
    if (resultDiv) {
        resultDiv.style.display = 'none';
    }
}

/**
 * ページを離れる際のクリーンアップ処理
 */
window.addEventListener('beforeunload', function() {
    stopScanning();
});

/**
 * ページが非表示になった際の処理（モバイル端末でのバックグラウンド移行など）
 */
document.addEventListener('visibilitychange', function() {
    if (document.hidden && scanning) {
        // ページが非表示になったらスキャンを停止
        stopScanning();
    }
});
