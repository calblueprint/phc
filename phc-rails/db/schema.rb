# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20150505021012) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"
  enable_extension "pg_trgm"

  create_table "accounts", force: :cascade do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "sf_id",                                     limit: 255
    t.string   "FirstName",                                 limit: 255
    t.string   "LastName",                                  limit: 255
    t.string   "SS_Num__c",                                 limit: 255
    t.string   "Birthdate__c",                              limit: 255
    t.string   "Phone",                                     limit: 255
    t.string   "PersonEmail",                               limit: 255
    t.string   "Gender__c",                                 limit: 255
    t.boolean  "Identify_as_GLBT__c"
    t.string   "Race__c",                                   limit: 255
    t.string   "Primary_Language__c",                       limit: 255
    t.boolean  "Foster_Care__c"
    t.boolean  "Veteran__c"
    t.string   "Housing_Status_New__c",                     limit: 255
    t.string   "How_long_have_you_been_homeless__c",        limit: 255
    t.string   "Where_do_you_usually_go_for_healthcare__c", limit: 255
    t.string   "Medical_Care_Other__c",                     limit: 255
  end

  add_index "accounts", ["FirstName"], name: "index_accounts_on_FirstName", using: :btree
  add_index "accounts", ["LastName"], name: "index_accounts_on_LastName", using: :btree

  create_table "event_registrations", force: :cascade do |t|
    t.string   "account_sfid", limit: 255
    t.string   "phc_sfid",     limit: 255
    t.string   "FirstName",    limit: 255
    t.string   "LastName",     limit: 255
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "Number__c",    limit: 255
  end

  create_table "event_registrations_services", id: false, force: :cascade do |t|
    t.integer "event_registration_id"
    t.integer "service_id"
  end

  add_index "event_registrations_services", ["event_registration_id", "service_id"], name: "join_table_index", unique: true, using: :btree

  create_table "last_modifieds", force: :cascade do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "last_modified", limit: 255
  end

  create_table "services", force: :cascade do |t|
    t.string   "name",       limit: 255
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "status",                 default: 0, null: false
  end

  create_table "users", force: :cascade do |t|
    t.string   "name",            limit: 255
    t.string   "email",           limit: 255
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "password_digest", limit: 255
    t.string   "auth_digest",     limit: 255
  end

  add_index "users", ["email"], name: "index_users_on_email", unique: true, using: :btree

end
