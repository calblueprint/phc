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
#  updated                                   :boolean          default(TRUE), not null
#

class Account < ActiveRecord::Base
  has_many :event_registrations, :dependent => :destroy

  FIELDS = [:sf_id, "FirstName","LastName","SS_Num__c","Birthdate__c","Phone","PersonEmail","Gender__c","Identify_as_GLBT__c",
      "Race__c", "Primary_Language__c", "Foster_Care__c","Veteran__c","Housing_Status_New__c","How_long_have_you_been_homeless__c",
      "Where_do_you_usually_go_for_healthcare__c","Medical_Care_Other__c"]

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
    Account.where(updated: false)
  end

  def to_hash
    {
      name: full_name,
      birthday: birthdate,
      sf_id: self.sf_id,
    }
  end

  def full_name
    "#{self.FirstName} #{self.LastName}"
  end

  def birthdate()
    return "#N/A" if self.Birthdate__c.nil?

    year, month, day = self.Birthdate__c.split("-")

    month ||= "01"
    day ||= "01"

    year = "19#{year}" if year.length == 2
    month = "0#{month}" if month.length == 1
    day = "0#{day}" if day.length == 1

    day_string = "#{year}-#{month}-#{day}"
    day_string.length == 10 ? day_string : "#N/A"
  end

end
