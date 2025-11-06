package com.example.medicare

import com.google.firebase.database.PropertyName

data class Doctor(
    @get:PropertyName("uid") @set:PropertyName("uid") var uid: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String = "",
    @get:PropertyName("phone") @set:PropertyName("phone") var phone: String = "",
    @get:PropertyName("address") @set:PropertyName("address") var address: String = "",
    @get:PropertyName("dob") @set:PropertyName("dob") var dob: String = "",
    @get:PropertyName("gender") @set:PropertyName("gender") var gender: String = "",
    @get:PropertyName("role") @set:PropertyName("role") var role: String = "",
    @get:PropertyName("hospitalName") @set:PropertyName("hospitalName") var hospitalName: String = "",
    @get:PropertyName("specialization") @set:PropertyName("specialization") var specialization: String = "",
    @get:PropertyName("briefDescription") @set:PropertyName("briefDescription") var briefDescription: String = "",
    @get:PropertyName("yearsOfExperience") @set:PropertyName("yearsOfExperience") var yearsOfExperience: String = "",
    @get:PropertyName("consultationFee") @set:PropertyName("consultationFee") var consultationFee: String = "",
    @get:PropertyName("profileImageUrl") @set:PropertyName("profileImageUrl") var profileImageUrl: String = "",
    @get:PropertyName("licenseImageUrl") @set:PropertyName("licenseImageUrl") var licenseImageUrl: String = "",
    @get:PropertyName("accountholdername") @set:PropertyName("accountholdername") var accountholdername: String = "",
    @get:PropertyName("accountnumber") @set:PropertyName("accountnumber") var accountnumber: String = "",
    @get:PropertyName("bankname") @set:PropertyName("bankname") var bankname: String = "",
    @get:PropertyName("branchname") @set:PropertyName("branchname") var branchname: String = "",
    @get:PropertyName("ifsccode") @set:PropertyName("ifsccode") var ifsccode: String = "",
    @get:PropertyName("bankaddress") @set:PropertyName("bankaddress") var bankaddress: String = "",
    @get:PropertyName("accounttype") @set:PropertyName("accounttype") var accounttype: String = "",
    @get:PropertyName("timestamp") @set:PropertyName("timestamp") var timestamp: String = ""
)
