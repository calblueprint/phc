class AccountsController < ApplicationController
  include Databasedotcom::Rails::Controller

  def index
    if not LastModified.any?
      LastModified.create(last_modified_datetime: Time.now)
      @accounts = Account.all
      @accounts.each do |a|
        @pa = PersonAccount.new
        @pa.first_name = a.FirstName
        @pa.last_name = a.LastName
        lmd = a.LastModifiedDate
        @pa.save
      end
      puts lmd
    else
      @last = LastModified.first
      puts "LastModifiedDate > " + @last.last_modified_datetime.to_s
      Account.query("LastModifiedDate > " + @last.last_modified_datetime.to_s + "")
      @last.update_attributes(last_modified_datetime: Time.now)
    end
  end

  def show
    @account = Account.find(params[:id])
  end
end
