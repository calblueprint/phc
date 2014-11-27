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

ActiveRecord::Schema.define(version: 20141127031723) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"
  enable_extension "pg_trgm"

  create_table "last_modifieds", force: true do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "last_modified"
  end

  create_table "person_accounts", force: true do |t|
    t.string   "first_name"
    t.string   "last_name"
    t.datetime "birthday"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "sf_id"
  end

  add_index "person_accounts", ["first_name"], name: "index_person_accounts_on_first_name", using: :btree
  add_index "person_accounts", ["last_name"], name: "index_person_accounts_on_last_name", using: :btree
  add_index "person_accounts", ["sf_id"], name: "index_person_accounts_on_sf_id", unique: true, using: :btree

end
