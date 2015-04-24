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
    groups = []
    attr_names = ""
    if attrs == []
      # Default is to match on SSN
      result = Account.select('"SS_Num__c"').group('"SS_Num__c"').having("count(*)>1")
      result[cursor..cursor+count].each do |account|
        accounts = Account.where(SS_Num__c: account.SS_Num__c)
        groups.append(accounts.map(&:to_hash))
      end
      attr_names = "SSN"
    else
      # Escape each attribute with quotes to prevent lowercasing by Rails
      attr_names = attrs
      attrs.map! { |x| "\"#{x}\"" }
      result = Account.select(attrs).group(attrs).having("count(*)>1")
      result[cursor..cursor+count].each do |account|
        attrs = account.attributes
        attrs.delete('id')
        accounts = Account.where(attrs)
        groups.append(accounts.map(&:to_hash))
      end
    end

    return groups, attr_names
  end

  def self.find_new_accounts()
    # Returns a list of new accounts made at the last PHC event, aka ones with no Salesforce ID

  end

  def to_hash
    first = (self.FirstName.nil? || self.FirstName.empty?) ? "(None)" : self.FirstName
    last = (self.LastName.nil? || self.FirstName.empty?) ? "" : self.LastName
    ssn = self.SS_Num__c.nil? ? "" : "x"*6 + (self.SS_Num__c[-4..-1] || self.SS_Num__c)
    birthday = self.Birthdate__c
    {name: name(first,last), ssn: ssn, birthday: birthday, sf_id: self.sf_id, created_at: self.created_at}
  end

  def name(first, last)
    "#{first} #{last}"
  end

end
