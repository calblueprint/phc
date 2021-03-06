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

ActiveRecord::Schema.define(version: 20150926222615) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"
  enable_extension "pg_trgm"

  create_table "accounts", force: :cascade do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "sf_id"
    t.string   "FirstName"
    t.string   "LastName"
    t.string   "SS_Num__c"
    t.string   "Birthdate__c"
    t.string   "Phone"
    t.string   "PersonEmail"
    t.string   "Gender__c"
    t.boolean  "Identify_as_GLBT__c"
    t.string   "Race__c"
    t.string   "Primary_Language__c"
    t.boolean  "Foster_Care__c"
    t.boolean  "Veteran__c"
    t.string   "Housing_Status_New__c"
    t.string   "How_long_have_you_been_homeless__c"
    t.string   "Where_do_you_usually_go_for_healthcare__c"
    t.string   "Medical_Care_Other__c"
    t.boolean  "updated",                                   default: true, null: false
  end

  add_index "accounts", ["FirstName"], name: "index_accounts_on_FirstName", using: :btree
  add_index "accounts", ["LastName"], name: "index_accounts_on_LastName", using: :btree

  create_table "event_registrations", force: :cascade do |t|
    t.string   "account_id"
    t.string   "phc_sfid"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "Number__c"
    t.integer  "Experience__c"
    t.text     "Services_Needed__c"
    t.text     "Feedback__c"
  end

  create_table "event_registrations_services", id: false, force: :cascade do |t|
    t.integer "event_registration_id"
    t.integer "service_id"
  end

  add_index "event_registrations_services", ["event_registration_id", "service_id"], name: "join_table_index", unique: true, using: :btree

  create_table "last_modifieds", force: :cascade do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "last_modified"
  end

  create_table "services", force: :cascade do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "status",     default: 0, null: false
  end

  create_table "users", force: :cascade do |t|
    t.string   "name"
    t.string   "email"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "password_digest"
    t.string   "auth_digest"
  end

  add_index "users", ["email"], name: "index_users_on_email", unique: true, using: :btree

end
