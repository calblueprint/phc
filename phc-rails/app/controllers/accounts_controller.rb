class AccountsController < ApplicationController
  include Databasedotcom::Rails::Controller

  def pull
    if not LastModified.any?
      LastModified.create(last_modified_datetime: Time.now)
      @accounts = Account.all
    else
      @last = LastModified.first
      #Account.query("LastModifiedDate > " + @last.last_modified_datetime.to_s + "")
      @accounts = Account.all #Temporary
      @last.update_attributes(last_modified_datetime: Time.now)
    end
    update_db @accounts
    redirect_to root_path
  end

  private

  def update_db(accounts)
    accounts.each do |a|
      pa = PersonAccount.find_or_initialize_by(sf_id: a.Id)
      pa.update(first_name: a.FirstName, last_name: a.LastName, birthday: a.PersonBirthdate)
      pa.save
    end
  end
end
