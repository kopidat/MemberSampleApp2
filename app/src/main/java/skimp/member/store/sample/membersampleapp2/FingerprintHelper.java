package skimp.member.store.sample.membersampleapp2;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.util.Log;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

public class FingerprintHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private static final String TAG = FingerprintHelper.class.getSimpleName();

    private CancellationSignal mCancellationSignal;

    private boolean mSelfCancelled;

    private Callback mCallback;
    public interface  Callback {
        void onAuthenticationSucceeded();
        void onAuthenticationFailed(int failMsgId, CharSequence failString);
        void onAuthenticationError(int errMsgId, CharSequence errString);
    }

    public boolean isFingerprintAuthAvailable(Context context) {
        final FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);
        boolean isAvailable = fingerprintManagerCompat.isHardwareDetected() && fingerprintManagerCompat.hasEnrolledFingerprints();
        Log.d(TAG, "fingerprintManagerCompat.isHardwareDetected() = " + fingerprintManagerCompat.isHardwareDetected());
        Log.d(TAG, "fingerprintManagerCompat.hasEnrolledFingerprints() = " + fingerprintManagerCompat.hasEnrolledFingerprints());
        Log.d(TAG, "isAvailable = " + isAvailable);
        return isAvailable;
    }

    public void startListening(Context context, Callback callback) {
        Log.i(TAG, "startListening(Context context, Callback callback)");
        Log.d(TAG, "callback = " + callback);

        mCallback = callback;

        if(!isFingerprintAuthAvailable(context)) {
            return;
        }

        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;

        final FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);
        fingerprintManagerCompat.authenticate(null, 0, mCancellationSignal, this, null);
    }

    public void stopListening() {
        Log.i(TAG, "stopListening()");

        if(mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Log.i(TAG, "onAuthenticationError(int errMsgId, CharSequence errString)");
        Log.d(TAG, "errMsgId / errString = " + errMsgId + " / " + errString);
        super.onAuthenticationError(errMsgId, errString);
        if(!mSelfCancelled) {
            mCallback.onAuthenticationError(errMsgId, errString);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Log.i(TAG, "onAuthenticationHelp(int helpMsgId, CharSequence helpString)");
        Log.d(TAG, "helpMsgId / helpString = " + helpMsgId + " / " + helpString);

        super.onAuthenticationHelp(helpMsgId, helpString);
        mCallback.onAuthenticationFailed(helpMsgId, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        Log.i(TAG, "onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result)");
        Log.d(TAG, "result = " + result);
        super.onAuthenticationSucceeded(result);

        mCallback.onAuthenticationSucceeded();
    }

    @Override
    public void onAuthenticationFailed() {
        Log.i(TAG, "onAuthenticationFailed()");
        super.onAuthenticationFailed();
        mCallback.onAuthenticationFailed(-1, null);
    }
}