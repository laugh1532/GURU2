package com.example.googlelogin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient
    //private const val TAG = "GoogleActivity"
    private val RC_SIGN_IN = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //FirebaseAuth 인스턴스(객체) 초기화
        firebaseAuth = FirebaseAuth.getInstance()

        //google 로그인 버튼
        btn_google.setOnClickListener { signIn() }

        btn_logout.setOnClickListener { view ->
            signOut()
        }

        btn_revokeAccess.setOnClickListener { view ->
            revokeAccess()
        }

        //google 로그인 옵션 구성. requestIdToken 및 email 요청
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



    }
    //onstart 활동 초기화할 때 사용자가 현재 로그인 되어 있는 지 확인
    public override fun onStart() {
        super.onStart()
        //val currentUser = firebaseAuth.currentUser
        //updateUI(currentUser)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account!=null){
            //toResultActivity(firebaseAuth.currentUser)
        }
    }
    //onstart end

    @SuppressLint("MissingSuperCall")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException){
                Log.w("MainActivity", "Google sign in failed", e)
            }
        }
    } //onActivityresult end

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount){
        Log.d("MainActivity", "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if(task.isSuccessful){
                    Log.w("MainActivity", "firebaseAuthWithGoogle 성공", task.exception)
                    toResultActivity(firebaseAuth?.currentUser)
                }else{
                    Log.w("MainActivity", "firebaseAuthWithGoogle 실패", task.exception)
                    //Snackbar.make(login_layout, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }// firebaseAuthWithGoogle END

    //toResultActivity
    fun toResultActivity(user: FirebaseUser?) {
        if(user != null){
            startActivity(Intent(this, /*ThirdActivity*/XmlParsingActivity::class.java))
            finish()
        }
    }//toResultActivity end

    //signin
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    //signin End

    private fun signOut() { // 로그아웃
        // Firebase sign out
        firebaseAuth.signOut()
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
        }
    }

    private fun revokeAccess() { //회원탈퇴
        // Firebase sign out
        firebaseAuth.signOut()
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {

        }
    }


}