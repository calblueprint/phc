class AccountsController < ApplicationController
  respond_to :json
  skip_before_action :verify_authenticity_token
  #before_action :verify_security_token

  def duplicates
    # TEMP AUTH TOKEN FOR DEMOS
    auth_token = request.params[:auth_token]
    cursor = request.params[:cursor]
    count = request.params[:count]
    cursor = cursor.nil? ? 0 : cursor.to_i
    count = count.nil? ? 20 : count.to_i
    if auth_token != ENV['AUTH_TOKEN']
      @accounts = []
      return
    end
    attrs = request.params[:attributes]
    attrs = attrs.nil? ? ["SS_Num__c"] : ActiveSupport::JSON.decode(attrs)

    # Account.all(:conditions => { :created_at => (Time.now.midnight - 1.day)..Time.now.midnight})
    @groups = Account.find_duplicates_by(attrs, count, cursor)
    @title = "Duplicate records by #{attrs.join(',')}"
  end

  def post_to_salesforce
    username = ENV["sf_username"]
    password = ENV["sf_password"] + ENV["sf_security_token"]

    # Make sure to remove the true argument in production.
    salesforce = SalesforceBulk::Api.new(username, password, true)

  end
end
