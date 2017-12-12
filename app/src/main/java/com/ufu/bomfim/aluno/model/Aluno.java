package com.ufu.bomfim.aluno.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.ufu.bomfim.aluno.dao.CustomDao;

@DatabaseTable(tableName = Aluno.TABLE_NAME_ALUNOS, daoClass = CustomDao.class)
public class Aluno {

    public static final String TABLE_NAME_ALUNOS = "alunos";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_NOME = "nome";
    public static final String FIELD_NAME_TELEFONE = "telefone";
    public static final String FIELD_NAME_SITE = "site";
    public static final String FIELD_NAME_EMAIL = "email";
    public static final String FIELD_NAME_ENDEREÇO = "endereco";
    public static final String FIELD_NAME_NOTA = "nota";
    public static final String FIELD_IMAGE_PATH = "image";

    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int Id;

    @DatabaseField(columnName = FIELD_NAME_NOME)
    private String Nome;

    @DatabaseField(columnName = FIELD_NAME_TELEFONE)
        private String Telefone;

    @DatabaseField(columnName = FIELD_NAME_SITE)
        private String Site;

    @DatabaseField(columnName = FIELD_NAME_EMAIL)
        private String Email;

    @DatabaseField(columnName = FIELD_NAME_ENDEREÇO)
        private String Endereco;

    @DatabaseField(columnName = FIELD_NAME_NOTA)
        private int Nota;

    @DatabaseField(columnName = FIELD_IMAGE_PATH)
        private String imagePath;

    public Aluno() {
        //needed by ORMLite.
    }

    /** Getters & Setters **/
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {Nome = nome;}

    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String telefone) {
        Telefone = telefone;
    }

    public String getSite() {
        return Site;
    }

    public void setSite(String site) {
        Site = site;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getEndereco() {
        return Endereco;
    }

    public void setEndereco(String endereco) {
        Endereco = endereco;
    }

    public int getNota() {
        return Nota;
    }

    public void setNota(int nota) {
        Nota = nota;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean hasImage() {
        return getImagePath() != null && !getImagePath().isEmpty();
    }
}
