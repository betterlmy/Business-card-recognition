package com.example.demo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.demo.Gson.gsonExample;
import com.example.demo.QR.qrMainActivity;
import com.example.demo.bean.Contact;

public class AddContactActivity extends AppCompatActivity {

    private static String TAG="contact";
    Button btnAdd, btnCreate;
    EditText etName,etEmail,etPhone,etCompany,etAddress,etTitles;
    Contact contact;
    ImageView qrview;
    private static Uri contactURI=ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();
//        contact=(Contact) getIntent().getSerializableExtra("contact");
        contact=new Contact();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        contact=(Contact) bundle.get("contact");
        etName.setText(contact.getName());
        etCompany.setText(contact.getCompany());
        etPhone.setText(contact.getPhone());
        etAddress.setText(contact.getAddr());
        etEmail.setText(contact.getEmail());
        etTitles.setText(contact.getTitles());
    }

    private void initView() {
        btnAdd=findViewById(R.id.btn_add_contact);
        btnCreate =findViewById(R.id.btn_CreateQR);
        qrview=findViewById(R.id.qrView);
        etName=findViewById(R.id.et_name);
        etEmail=findViewById(R.id.et_email);
        etAddress=findViewById(R.id.et_address);
        etPhone=findViewById(R.id.et_phone);
        etCompany=findViewById(R.id.et_organization);
        etTitles=findViewById(R.id.et_titles);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact.setName(etName.getText().toString());
                contact.setCompany(etCompany.getText().toString());
                contact.setAddr(etAddress.getText().toString());
                contact.setEmail(etEmail.getText().toString());
                contact.setPhone(etPhone.getText().toString());
                contact.setTitles(etTitles.getText().toString());

                try {
                    addContact(contact);
                    Log.e(TAG, "onClick: 添加成功" );
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact.setName(etName.getText().toString());
                contact.setCompany(etCompany.getText().toString());
                contact.setAddr(etAddress.getText().toString());
                contact.setEmail(etEmail.getText().toString());
                contact.setPhone(etPhone.getText().toString());
                contact.setTitles(etTitles.getText().toString());
                contact.setFlag("lmy");
                String json=gsonExample.toGson(contact);
                //生成二维码显示在imageView上
                Bitmap bitmap= qrMainActivity.createCode(json);
                qrview.setImageBitmap(bitmap);
            }
        });
    }


    //添加到联系人，将该activity的全局变量按分类添加到系统通讯录数据库中
    public void addContact(Contact contact) throws Exception{
        String name,email,phone,company,address,titles;
        name=contact.getName();
        email=contact.getEmail();
        phone=contact.getPhone();
        address=contact.getAddr();
        company=contact.getCompany();
        titles=contact.getTitles();

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");//系统通讯录所在数据库
        ContentResolver resolver =getContentResolver();
        ContentValues values = new ContentValues();
        long contactid = ContentUris.parseId(resolver.insert(uri, values));
        Log.e(TAG, "id"+contactid);
        uri = Uri.parse("content://com.android.contacts/data");

        //添加姓名
        values.put("raw_contact_id", contactid);
        values.put(ContactsContract.Contacts.Data.MIMETYPE, "vnd.android.cursor.item/name");
        values.put("data1", name);
        resolver.insert(uri, values);
        values.clear();

        //添加手机
        values.put("raw_contact_id", contactid);
        values.put(ContactsContract.Contacts.Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
        values.put("data1", phone);
        resolver.insert(uri, values);
        values.clear();

        //添加Email
        values.put("raw_contact_id", contactid);
        values.put(ContactsContract.Contacts.Data.MIMETYPE, "vnd.android.cursor.item/email_v2");
        values.put("data1", email);
        resolver.insert(uri, values);
        values.clear();
        //添加公司
        values.put("raw_contact_id", contactid);
        values.put(ContactsContract.Contacts.Data.MIMETYPE, "vnd.android.cursor.item/organization");
        values.put("data1", company);
        resolver.insert(uri, values);
        values.clear();
        //添加地址
        values.put("raw_contact_id", contactid);
        values.put(ContactsContract.Contacts.Data.MIMETYPE, "vnd.android.cursor.item/postal-address_v2");
        values.put("data1", address);
        resolver.insert(uri, values);
        values.clear();
        //添加职称
        values.put("raw_contact_id", contactid);
        values.put(ContactsContract.Contacts.Data.MIMETYPE, "vnd.android.cursor.item/organization");
        values.put("data2", titles);
        resolver.insert(uri, values);
        values.clear();

        Toast.makeText(this,"添加成功",Toast.LENGTH_LONG).show();
//        Intent intent=new Intent(AddContactActivity.this,MainActivity.class);
//        startActivity(intent);
    }

}