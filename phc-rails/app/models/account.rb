# == Schema Information
#
# Table name: accounts
#
#  id                                        :integer          not null, primary key
#  created_at                                :datetime
#  updated_at                                :datetime
#  sf_id                                     :string(255)
#  FirstName                                 :string(255)
#  LastName                                  :string(255)
#  SS_Num__c                                 :string(255)
#  Birthdate__c                              :string(255)
#  Phone                                     :string(255)
#  PersonEmail                               :string(255)
#  Gender__c                                 :string(255)
#  Identify_as_GLBT__c                       :boolean
#  Race__c                                   :string(255)
#  Primary_Language__c                       :string(255)
#  Foster_Care__c                            :boolean
#  Veteran__c                                :boolean
#  Housing_Status_New__c                     :string(255)
#  How_long_have_you_been_homeless__c        :string(255)
#  Where_do_you_usually_go_for_healthcare__c :string(255)
#  Medical_Care_Other__c                     :string(255)
#

class Account < ActiveRecord::Base
  has_many :event_registrations

  @fields = [:sf_id, "FirstName","LastName","SS_Num__c","Birthdate__c","Phone","PersonEmail","Gender__c","Identify_as_GLBT__c",
      "Race__c", "Primary_Language__c", "Foster_Care__c","Veteran__c","Housing_Status_New__c","How_long_have_you_been_homeless__c",
      "Where_do_you_usually_go_for_healthcare__c","Medical_Care_Other__c"]

  # I'm not sure if calling this 'create' will overwrite something and cause funky behavior, so it's called spawn 8)
  def self.spawn(params)
    # NOTE, new accounts for now will not have an Salesforce ID associated with them until we
    # implement posting to Salesforce. Therefore, for now, sf_id can be empty, and we will
    # match an event registration to an account through the Rails ID primary key.
    account = Account.new
    @fields.each do |key|
      account[key] = params[key]
    end
    if account.save then account else nil end

    #### TODO: POST TO SALESFORCE #####
  end

  def self.find_duplicates_by(attrs, count, cursor)
    groups = {}
    # Escape each attribute with quotes to prevent lowercasing by Rails
    attrs.map! { |x| "\"#{x}\"" }
    result = Account.select(attrs).group(attrs).having("count(*)>1")
    result[cursor..cursor+count].each do |account|
      fields = account.attributes
      fields.delete('id')
      accounts = Account.where(fields)
      groups[fields] = accounts.map(&:to_hash)
    end
    groups
  end

  def self.find_new_accounts()
    # Returns a list of new accounts made at the last PHC event, aka ones with no Salesforce ID
    Account.where(sf_id: nil)
  end

  def to_hash
    {
      name: name(self.FirstName, self.LastName),
      ssn: ssn(self.SS_Num__c),
      birthday: self.Birthdate__c,
      sf_id: self.sf_id,
      created_at: self.created_at,
      phone: self.Phone
    }
  end

  def name(first, last)
    if first.nil? or first.empty?
      first = "(None)"
    end
    if last.nil?
      last = ""
    end
    "#{first} #{last}"
  end

  def ssn(ssn)
    if ssn.nil? or ssn.empty?
      "(None)"
    elsif ssn.length < 9
      ssn
    else
      "x"*6 + self.SS_Num__c[-4..-1]
    end
  end

  def birthdate()
    if self.Birthdate__c.nil? then return "#N/A" end
    year, month, day = self.Birthdate__c.split("-")
    if year.nil? then year = "1900" end
    if month.nil? then month = "01" end
    if day.nil? then day = "01" end

    if year.length == 2
      year = "19" + year
    end
    if month.length == 1
      month = "0" + month
    end
    if day.length == 1
      day = "0" + day
    end

    day_string = "#{year}-#{month}-#{day}"
    if "#{day_string}".length != 10
      "#N/A"
    else
      day_string
    end
  end
end
