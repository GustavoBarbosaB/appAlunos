package com.ufu.bomfim.aluno.helper;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;
import com.ufu.bomfim.aluno.model.Aluno;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AlunoDbHelper {

    private static AlunoDbHelper alunoDbHelper;
    private static DatabaseHelper dbHelper;

    private AlunoDbHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static void init(Context context) {
        if (alunoDbHelper == null) {
            alunoDbHelper = new AlunoDbHelper(context);
        }
    }

    public static AlunoDbHelper getInstance() {
        return alunoDbHelper;
    }

    private synchronized void createIfNotExists(Aluno aluno) {
        try {
            dbHelper.getAlunoDao().createIfNotExists(aluno);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void  createOrUpdate(Aluno aluno) {
        Aluno alunoById = getAlunoById(aluno.getId());
        if(alunoById == null) {
            createIfNotExists(aluno);
        } else {
            update(aluno);
        }
    }

    private synchronized void  update(Aluno aluno) {
        try {
            dbHelper.getAlunoDao().update(aluno);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private synchronized void  delete(Aluno aluno) {
        try {
            dbHelper.getAlunoDao().delete(aluno);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void  deleteById(int alunoId) {
        Aluno aluno = getAlunoById(alunoId);
        if(aluno != null) {
            delete(aluno);
        }
    }

    public synchronized Aluno getAlunoById(int alunoId) {
        Aluno aluno = null;
        try {
            aluno = dbHelper.getAlunoDao().queryForId(alunoId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aluno;
    }

    private synchronized Aluno getAlunoByPhoneNumber(String phoneNumber) {
        Aluno aluno = null;
        try {
            QueryBuilder<Aluno, Integer> queryBuilder = dbHelper.getAlunoDao().queryBuilder();
            aluno = queryBuilder.where().eq(Aluno.FIELD_NAME_TELEFONE,phoneNumber).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aluno;
    }

    public synchronized List<Aluno> getAllAlunos(){
        try {
            return dbHelper.getAlunoDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
